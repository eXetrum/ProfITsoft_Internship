package dev.profitsoft.intership;

import com.jayway.jsonpath.JsonPath;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        authorRepository.deleteAll();
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

    @Test
    public void testCreateBookDuplicate() throws Exception {
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

        mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testCreateBookAuthorNotFound() throws Exception {
        String body = """
               {
                    "title": "test book",
                    "genre": "fiction",
                    "publishYear": 1999,
                    "authorId": "random_author_id_which_is_not_found"
               }
               """;
        mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isNotFound());

    }

    @Test
    public void testCreateBookBadFormat() throws Exception {
        String body = """
               {
                    "title-title": "test book",
                    "genre-baz": "fiction",
                    "publishYear-buz": 1999
               }
               """;
        mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testGetBookById() throws Exception {
        String body = """
               {
                    "title": "test book",
                    "genre": "fiction",
                    "publishYear": 1999,
                    "authorId": "%s"
               }
               """.formatted(defaultAuthors.get(0).getId());
       MvcResult result = mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String id = JsonPath.read(jsonResponse, "$.result");

        mvc.perform(get("/api/book/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("test book"))
                .andExpect(jsonPath("$.genre").value("fiction"))
                .andExpect(jsonPath("$.publishYear").value("1999"))
                .andExpect(jsonPath("$.author.name").value(defaultAuthors.get(0).getName()))
                .andExpect(jsonPath("$.author.birthdayYear").value(defaultAuthors.get(0).getBirthdayYear()))
                .andExpect(jsonPath("$.author.id").value(defaultAuthors.get(0).getId()));

    }

    @Test
    public void testGetBookByIdNotFound() throws Exception {
        mvc.perform(get("/api/book/random_id")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

    }

    @Test
    public void testUpdateBookById() throws Exception {
        String body = """
               {
                    "title": "test book",
                    "genre": "fiction",
                    "publishYear": 1999,
                    "authorId": "%s"
               }
               """.formatted(defaultAuthors.get(0).getId());
        MvcResult result = mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String id = JsonPath.read(jsonResponse, "$.result");

        mvc.perform(get("/api/book/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("test book"))
                .andExpect(jsonPath("$.genre").value("fiction"))
                .andExpect(jsonPath("$.publishYear").value("1999"))
                .andExpect(jsonPath("$.author.name").value(defaultAuthors.get(0).getName()))
                .andExpect(jsonPath("$.author.birthdayYear").value(defaultAuthors.get(0).getBirthdayYear()))
                .andExpect(jsonPath("$.author.id").value(defaultAuthors.get(0).getId()));


        mvc.perform(put("/api/book/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "title": "NEW test book",
                        "genre": "horror",
                        "publishYear": 1111,
                        "authorId": "%s"
                        }
                        """.formatted(defaultAuthors.get(1).getId()))
                )
                .andExpect(status().isOk());


        mvc.perform(get("/api/book/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("NEW test book"))
                .andExpect(jsonPath("$.genre").value("horror"))
                .andExpect(jsonPath("$.publishYear").value("1111"))
                .andExpect(jsonPath("$.author.name").value(defaultAuthors.get(1).getName()))
                .andExpect(jsonPath("$.author.birthdayYear").value(defaultAuthors.get(1).getBirthdayYear()))
                .andExpect(jsonPath("$.author.id").value(defaultAuthors.get(1).getId()));

    }

    @Test
    public void testUpdateBookByIdNotFound() throws Exception {
        mvc.perform(put("/api/book/random_id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "title": "NEW test book",
                        "genre": "horror",
                        "publishYear": 1111,
                        "authorId": "%s"
                        }
                        """.formatted(defaultAuthors.get(1).getId()))
                )
                .andExpect(status().isNotFound());

    }

    @Test
    public void testUpdateBookByIdAuthorNotFound() throws Exception {
        String body = """
               {
                    "title": "test book",
                    "genre": "fiction",
                    "publishYear": 1999,
                    "authorId": "%s"
               }
               """.formatted(defaultAuthors.get(0).getId());
        MvcResult result = mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String id = JsonPath.read(jsonResponse, "$.result");

        mvc.perform(get("/api/book/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("test book"))
                .andExpect(jsonPath("$.genre").value("fiction"))
                .andExpect(jsonPath("$.publishYear").value("1999"))
                .andExpect(jsonPath("$.author.name").value(defaultAuthors.get(0).getName()))
                .andExpect(jsonPath("$.author.birthdayYear").value(defaultAuthors.get(0).getBirthdayYear()))
                .andExpect(jsonPath("$.author.id").value(defaultAuthors.get(0).getId()));


        mvc.perform(put("/api/book/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "title": "NEW test book",
                        "genre": "horror",
                        "publishYear": 1111,
                        "authorId": "random_id"
                        }
                        """)
                )
                .andExpect(status().isNotFound());

    }

    @Test
    public void testDeleteBookById() throws Exception {
        String body = """
               {
                    "title": "test book",
                    "genre": "fiction",
                    "publishYear": 1999,
                    "authorId": "%s"
               }
               """.formatted(defaultAuthors.get(0).getId());
        MvcResult result = mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String id = JsonPath.read(jsonResponse, "$.result");

        mvc.perform(delete("/api/book/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteBookByIdNotFound() throws Exception {
        mvc.perform(delete("/api/book/random_id")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPaginationByAuthorNameDefaultPageSize() throws Exception {
        for(int i = 0; i < defaultAuthors.size(); ++i) {
             mvc.perform(post("/api/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                    "title": "test book #%d",
                                    "genre": "fiction",
                                    "publishYear": 1999,
                                    "authorId": "%s"
                                    }
                                    """.formatted(i + 1, defaultAuthors.get(i).getId()))
                    )
                    .andExpect(status().isCreated());
        }


        mvc.perform(post("/api/book/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "authorName": "default"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(5))
                // default 5 per page
                .andExpect(jsonPath("$.totalPages").value(1));

        mvc.perform(post("/api/book/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "authorName": "default"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(5))
                // default 5 per page
                .andExpect(jsonPath("$.totalPages").value(1))
                // Should be all books with author name that contains "default" substring, thus entire set
                // Check first and last
                .andExpect(jsonPath("$.list[0].title").value("test book #1"))
                .andExpect(jsonPath("$.list[0].author.id").value(defaultAuthors.get(0).getId()))
                .andExpect(jsonPath("$.list[4].title").value("test book #5"))
                .andExpect(jsonPath("$.list[4].author.id").value(defaultAuthors.get(4).getId()));

    }

    @Test
    public void testPaginationByAuthorNameSetPageSize() throws Exception {
        for(int i = 0; i < defaultAuthors.size(); ++i) {
            mvc.perform(post("/api/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                    "title": "test book #%d",
                                    "genre": "fiction",
                                    "publishYear": 1999,
                                    "authorId": "%s"
                                    }
                                    """.formatted(i + 1, defaultAuthors.get(i).getId()))
                    )
                    .andExpect(status().isCreated());
        }

        mvc.perform(post("/api/book/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "authorName": "default",
                          "size": "1"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(1))
                // Now expected 5 pages as total and 1 item per page
                .andExpect(jsonPath("$.totalPages").value(5))
                // Check first and last
                .andExpect(jsonPath("$.list[0].title").value("test book #1"))
                .andExpect(jsonPath("$.list[0].author.id").value(defaultAuthors.get(0).getId()));

    }

    @Test
    public void testPaginationByAuthorNameSetPageOffset() throws Exception {
        for(int i = 0; i < defaultAuthors.size(); ++i) {
            mvc.perform(post("/api/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                    "title": "test book #%d",
                                    "genre": "fiction",
                                    "publishYear": 1999,
                                    "authorId": "%s"
                                    }
                                    """.formatted(i + 1, defaultAuthors.get(i).getId()))
                    )
                    .andExpect(status().isCreated());
        }

        mvc.perform(post("/api/book/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "authorName": "default",
                          "size": "2",
                          "page": "0"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(2))
                .andExpect(jsonPath("$.totalPages").value(3)) // 2 + 2 + 1
                .andExpect(jsonPath("$.list[0].title").value("test book #1"))
                .andExpect(jsonPath("$.list[0].author.id").value(defaultAuthors.get(0).getId()))
                .andExpect(jsonPath("$.list[1].title").value("test book #2"))
                .andExpect(jsonPath("$.list[1].author.id").value(defaultAuthors.get(1).getId()));


        mvc.perform(post("/api/book/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "authorName": "default",
                          "size": "2",
                          "page": "1"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(2))
                .andExpect(jsonPath("$.totalPages").value(3)) // 2 + 2 + 1
                .andExpect(jsonPath("$.list[0].title").value("test book #3"))
                .andExpect(jsonPath("$.list[0].author.id").value(defaultAuthors.get(2).getId()))
                .andExpect(jsonPath("$.list[1].title").value("test book #4"))
                .andExpect(jsonPath("$.list[1].author.id").value(defaultAuthors.get(3).getId()));

        mvc.perform(post("/api/book/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "authorName": "default",
                          "size": "2",
                          "page": "2"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(1))
                .andExpect(jsonPath("$.totalPages").value(3)) // 2 + 2 + 1
                .andExpect(jsonPath("$.list[0].title").value("test book #5"))
                .andExpect(jsonPath("$.list[0].author.id").value(defaultAuthors.get(4).getId()));

    }

    @Test
    public void testPaginationByAuthorId() throws Exception {
        // AuthorId for default author #1
        for (int i = 0; i < 3; ++i) {
            mvc.perform(post("/api/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                    "title": "test book1 #%d",
                                    "genre": "fiction1",
                                    "publishYear": 1999,
                                    "authorId": "%s"
                                    }
                                    """.formatted(i + 1, defaultAuthors.get(0).getId()))
                    )
                    .andExpect(status().isCreated());
        }

        // AuthorId for default author #2
        for (int i = 0; i < 2; ++i) {
            mvc.perform(post("/api/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                    "title": "test book2 #%d",
                                    "genre": "fiction2",
                                    "publishYear": 1888,
                                    "authorId": "%s"
                                    }
                                    """.formatted(i + 1, defaultAuthors.get(1).getId()))
                    )
                    .andExpect(status().isCreated());
        }

        mvc.perform(post("/api/book/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "authorId": "%s"
                                }
                                """.formatted(defaultAuthors.get(0).getId()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.list[0].title").value("test book1 #1"))
                .andExpect(jsonPath("$.list[0].author.id").value(defaultAuthors.get(0).getId()))
                .andExpect(jsonPath("$.list[1].title").value("test book1 #2"))
                .andExpect(jsonPath("$.list[1].author.id").value(defaultAuthors.get(0).getId()))
                .andExpect(jsonPath("$.list[2].title").value("test book1 #3"))
                .andExpect(jsonPath("$.list[2].author.id").value(defaultAuthors.get(0).getId()));


        mvc.perform(post("/api/book/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "authorId": "%s"
                                }
                                """.formatted(defaultAuthors.get(1).getId()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.list[0].title").value("test book2 #1"))
                .andExpect(jsonPath("$.list[0].author.id").value(defaultAuthors.get(1).getId()))
                .andExpect(jsonPath("$.list[1].title").value("test book2 #2"))
                .andExpect(jsonPath("$.list[1].author.id").value(defaultAuthors.get(1).getId()));

    }

    @Test
    public void testReportBooks() throws Exception {
        for(int i = 0; i < defaultAuthors.size(); ++i) {
            mvc.perform(post("/api/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                    "title": "test book #%d",
                                    "genre": "fiction",
                                    "publishYear": 1999,
                                    "authorId": "%s"
                                    }
                                    """.formatted(i + 1, defaultAuthors.get(i).getId()))
                    )
                    .andExpect(status().isCreated());
        }

        MvcResult result = mvc.perform(post("/api/book/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "authorName": "default"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andReturn();



        String actual = result.getResponse().getContentAsString();
        String expected = "title,genre,publishYear,authorName\n" +
                "test book #1,fiction,1999,%s\n".formatted(defaultAuthors.get(0).getName()) +
                "test book #2,fiction,1999,%s\n".formatted(defaultAuthors.get(1).getName()) +
                "test book #3,fiction,1999,%s\n".formatted(defaultAuthors.get(2).getName()) +
                "test book #4,fiction,1999,%s\n".formatted(defaultAuthors.get(3).getName()) +
                "test book #5,fiction,1999,%s\n".formatted(defaultAuthors.get(4).getName());

        assertEquals(expected, actual);
    }

    @Test
    public void testUploadBooks() throws Exception {
        String jsonForImport = """
                [
                	{
                        "title": "Adolphe",
                		"genre": "NoIdea22222",
                        "author": {
                            "name": "Benjamin Constant",
                			"birthdayYear": 2011
                        },
                        "publishYear": 1844
                    },
                    {
                        "title": "20 Years at Hull House",
                        "genre": "Fiction",
                        "author": {
                            "name": "Jane Addams",
                			"birthdayYear": 1888
                        },
                        "publisYear": 1945
                    },
                    {
                        "title": "Max Ernst",
                        "genre": "Art",
                        "author": {
                			"name": "Max Ernst",
                			"birthdayYear": 1234
                		},
                        "publishYear": 1956
                    },
                	{
                        "title": "Max Super Mega Doctor Director",
                        "genre": "Super genre",
                        "author": {
                			"name": "Max",
                			"birthdayYear": 1010
                		},
                        "publishYear": 1111
                    },
                    {
                      "title": "Max Super Mega Doctor Director",
                      "genre": "Super genre",
                      "author": {
                        "name": "Max",
                        "birthdayYear": 1010
                      },
                      "publishYear": 1111
                    }
                ]
        """;

        MockMultipartFile fileUploadData = new MockMultipartFile(
                "file",
                "books.json",
                "application/json",
                jsonForImport.getBytes()
        );

        mvc.perform(
                MockMvcRequestBuilders.multipart("/api/book/upload")
                        .file((fileUploadData)
                ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").value("Saved: 4, Total: 5"));
    }

}
