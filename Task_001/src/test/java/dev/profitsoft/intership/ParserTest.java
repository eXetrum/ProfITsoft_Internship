package dev.profitsoft.intership;

import com.fasterxml.jackson.core.JsonParseException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    private static final String TEMP_FILE_EXT = ".json";
    private static final String correctSingleEntry = """
                [
                    {
                        "title": "super mega book",
                        "publish_year": 2000, 
                        "authors": ["a1", "a2", "a3"],
                        "subject": ["tag1", "tag2"]
                    }
                ]""";

    private static final String correctDuplicateEntry = """
                [
                    {
                        "title": "super mega book",
                        "publish_year": 2000,
                        "authors": ["a1", "a2", "a3"],
                        "subject": ["tag1", "tag2"]
                    },
                    {
                        "title": "super mega book",
                        "publish_year": 2000, 
                        "authors": ["a1", "a2", "a3"],
                        "subject": ["tag1", "tag2"]
                    }
                ]""";

    private static final String badFormat = """
            [
                {
                    "rnd_key1": 42,
                    "rnd_key2": null,
                },
                [1,2,3,4,5]
            ]
            """;
    private static final String missingFields = """
                [
                    {
                        "title": "super mega book1",
                        "publish_year": 2000,
                        "authors": ["a1", "a2", "a3"],
                        "subject": ["tag1", "tag2"]
                    },
                    {
                        "title": "super mega book2",
                        "publish_year": 2000,
                        "subject": ["tag1", "tag2"]
                    },
                    {
                        "title": "super mega book3",
                        "publish_year": 2000, 
                        "authors": ["a1", "a2", "a3"],
                        "subject": ["tag1", "tag2"]
                    }
                ]""";
    private static final String emptyArray = "[]";
    private static final String emptyJsonObject = "{}";

    private static File createTempFile(String filename, String content) throws IOException {
        Path tempFilePath = Files.createTempFile(filename, TEMP_FILE_EXT);
        Files.write(tempFilePath, content.getBytes());
        return tempFilePath.toFile();
    }

    @Test
    void testSingleEntry() throws Exception {
        try(Parser parser = new Parser(createTempFile("correctSingleEntry.json", correctSingleEntry))) {
            Book book = parser.next();

            assertTrue(book.isValidObject());
            assertEquals("super mega book", book.getTitle());
            assertEquals(2000, book.getPublishYear());
            assertArrayEquals(new String[]{"a1", "a2", "a3"}, book.getAuthors().toArray());
            assertArrayEquals(new String[]{"tag1", "tag2"}, book.getSubjects().toArray());
            assertNull(parser.next());
        }
    }

    @Test
    void testDuplicateEntry() throws Exception {
        try(Parser parser = new Parser(createTempFile("correctDuplicateEntry.json", correctDuplicateEntry))) {

            Book book = parser.next();
            assertTrue(book.isValidObject());
            assertEquals("super mega book", book.getTitle());
            assertEquals(2000, book.getPublishYear());
            assertArrayEquals(new String[]{"a1", "a2", "a3"}, book.getAuthors().toArray());
            assertArrayEquals(new String[]{"tag1", "tag2"}, book.getSubjects().toArray());

            book = parser.next();
            assertTrue(book.isValidObject());
            assertEquals("super mega book", book.getTitle());
            assertEquals(2000, book.getPublishYear());
            assertArrayEquals(new String[]{"a1", "a2", "a3"}, book.getAuthors().toArray());
            assertArrayEquals(new String[]{"tag1", "tag2"}, book.getSubjects().toArray());

            assertNull(parser.next());
        }
    }

    @Test
    void testBadFormat() throws Exception {
        try(Parser parser = new Parser(createTempFile("badFormat.json", badFormat))) {
            assertThrows(JsonParseException.class, parser::next);
        }
    }

    @Test
    void testMissingFields() throws Exception {
        try(Parser parser = new Parser(createTempFile("missingFields.json", missingFields))) {

            Book book = parser.next();

            assertTrue(book.isValidObject());
            assertEquals("super mega book1", book.getTitle());
            assertEquals(2000, book.getPublishYear());
            assertArrayEquals(new String[]{"a1", "a2", "a3"}, book.getAuthors().toArray());
            assertArrayEquals(new String[]{"tag1", "tag2"}, book.getSubjects().toArray());

            book = parser.next();
            assertFalse(book.isValidObject());

            book = parser.next();
            assertTrue(book.isValidObject());
            assertEquals("super mega book3", book.getTitle());
            assertEquals(2000, book.getPublishYear());
            assertArrayEquals(new String[]{"a1", "a2", "a3"}, book.getAuthors().toArray());
            assertArrayEquals(new String[]{"tag1", "tag2"}, book.getSubjects().toArray());

            assertNull(parser.next());
        }
    }

    @Test
    void testEmptyArray() throws Exception {
        try(Parser parser = new Parser(createTempFile("emptyArray.json", emptyArray))) {
            Book book = parser.next();
            assertNull(book);
        }
    }

    @Test
    void testEmptyJsonObject() {
        assertThrows(Exception.class, () -> {
            try (Parser parser = new Parser(createTempFile("emptyJsonObject.json", emptyJsonObject))) {}
        });
    }
}