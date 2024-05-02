package dev.profitsoft.intership;

import dev.profitsoft.intership.booklibrary.BookLibraryApplication;
import dev.profitsoft.intership.booklibrary.data.AuthorData;
import dev.profitsoft.intership.booklibrary.repository.AuthorRepository;
import dev.profitsoft.intership.booklibrary.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = BookLibraryApplication.class)
@AutoConfigureMockMvc
public class BookControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    private List<AuthorData> defaultAuthors = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        final int TOTAL_AUTHORS = 5;
        defaultAuthors.clear();

        for(int i = 0; i < TOTAL_AUTHORS; ++i) {
            AuthorData author = new AuthorData();
            author.setName("default author #" + (i + 1));
            author.setBirthdayYear(1000 + i + 1);
            author.setId(UUID.randomUUID().toString());
            author.setSavedAt(Instant.now());
            defaultAuthors.add(author);
        }
        authorRepository.saveAll(defaultAuthors);
    }

    @AfterEach
    public void afterEach() {
        bookRepository.deleteAll();
    }

    @Test
    public void testCreateBook() throws Exception {
        String body = """
               {
                    "title": "test book",
                    "genre": "fiction",
                    "publishYear": 1999,
                    "authorId": "%s"
               }
               """.formatted(defaultAuthors.get(0).getId());
        mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated());

    }
}
