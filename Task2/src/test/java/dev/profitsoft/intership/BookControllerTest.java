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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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

}
