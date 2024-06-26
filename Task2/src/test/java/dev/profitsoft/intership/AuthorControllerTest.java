package dev.profitsoft.intership;

import com.jayway.jsonpath.JsonPath;
import dev.profitsoft.intership.booklibrary.BookLibraryApplication;
import dev.profitsoft.intership.booklibrary.repository.AuthorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = BookLibraryApplication.class)
@AutoConfigureMockMvc
public class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void beforeEach() {
        authorRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        authorRepository.deleteAll();
    }

    @Test
    public void testCreateAuthor() throws Exception {
        String body = """
               {
                    "name": "test author",
                    "birthdayYear": 1111
               }
               """;
        mvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated());

    }

    @Test
    public void testCreateAuthorDuplicate() throws Exception {
        String body = """
               {
                    "name": "test author",
                    "birthdayYear": 1111
               }
               """;
        mvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated());

        mvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testCreateAuthorWrongFormat() throws Exception {
        String body = """
               {
                    "nanana": "test author",
                    "barbarbar": 1111
               }
               """;
        mvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testGetAllAuthors() throws Exception {
        final int TOTAL_RECORDS = 5;
        for(int i = 0; i < TOTAL_RECORDS; ++i) {
            String body = """
                    {
                         "name": "test author #%d",
                         "birthdayYear": 200%d
                    }
                    """.formatted(i + 1, i);

            mvc.perform(post("/api/author")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                    )
                    .andExpect(status().isCreated());
        }

        mvc.perform(get("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(5))
                .andExpect(jsonPath("$.list[0].name").value("test author #1"))
                .andExpect(jsonPath("$.list[1].name").value("test author #2"))
                .andExpect(jsonPath("$.list[2].name").value("test author #3"))
                .andExpect(jsonPath("$.list[3].name").value("test author #4"))
                .andExpect(jsonPath("$.list[4].name").value("test author #5"));
    }

    @Test
    public void testGetAuthorById() throws Exception {
        String body = """
               {
                    "name": "test author",
                    "birthdayYear": 1111
               }
               """;
        MvcResult result = mvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String id = JsonPath.read(jsonResponse, "$.result");

        mvc.perform(get("/api/author/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test author"))
                .andExpect(jsonPath("$.birthdayYear").value("1111"));
    }

    @Test
    public void testGetAuthorByIdNotFound() throws Exception {
        mvc.perform(get("/api/author/random_id")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateAuthorById() throws Exception {
        String body = """
               {
                    "name": "test author",
                    "birthdayYear": 1111
               }
               """;
        MvcResult result = mvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String id = JsonPath.read(jsonResponse, "$.result");

        mvc.perform(get("/api/author/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test author"))
                .andExpect(jsonPath("$.birthdayYear").value("1111"));

        mvc.perform(put("/api/author/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "name": "NEW test author",
                        "birthdayYear": 1212
                        }
                        """)
                )
                .andExpect(status().isOk());

        mvc.perform(get("/api/author/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NEW test author"))
                .andExpect(jsonPath("$.birthdayYear").value("1212"));
    }

    @Test
    public void testUpdateAuthorByIdDuplicateName() throws Exception {
        mvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                           {
                                "name": "SAME NAME AUTHOR",
                                "birthdayYear": 1111
                           }
                        """)
                )
                .andExpect(status().isCreated());

        MvcResult result = mvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                           {
                                "name": "test author 2",
                                "birthdayYear": 1888
                           }
                        """)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String id = JsonPath.read(jsonResponse, "$.result");

        mvc.perform(put("/api/author/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                        "name": "SAME NAME AUTHOR",
                        "birthdayYear": 1212
                        }
                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateAuthorByIdNotFound() throws Exception {
        mvc.perform(get("/api/author/random_id")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAuthorById() throws Exception {
        String body = """
               {
                    "name": "test author",
                    "birthdayYear": 1111
               }
               """;
        MvcResult result = mvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String id = JsonPath.read(jsonResponse, "$.result");

        mvc.perform(delete("/api/author/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteAuthorByIdNotFound() throws Exception {
        mvc.perform(delete("/api/author/random_id")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSearchAuthorsByNameWithPagination() throws Exception {
        final int TOTAL_RECORDS = 5;
        for(int i = 0; i < TOTAL_RECORDS; ++i) {
            String body = """
                    {
                         "name": "test author #%d",
                         "birthdayYear": 200%d
                    }
                    """.formatted(i + 1, i);

            mvc.perform(post("/api/author")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                    )
                    .andExpect(status().isCreated());
        }

        for(int i = 0; i < TOTAL_RECORDS * 2 - 1; ++i) {
            String body = """
                    {
                         "name": "secret author #%d",
                         "birthdayYear": 200%d
                    }
                    """.formatted(i + 1, i);

            mvc.perform(post("/api/author")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                    )
                    .andExpect(status().isCreated());
        }

        mvc.perform(post("/api/author/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "test"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(5))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.list[0].name").value("test author #1"))
                .andExpect(jsonPath("$.list[1].name").value("test author #2"))
                .andExpect(jsonPath("$.list[2].name").value("test author #3"))
                .andExpect(jsonPath("$.list[3].name").value("test author #4"))
                .andExpect(jsonPath("$.list[4].name").value("test author #5"));

        mvc.perform(post("/api/author/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "secret"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(5))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.list[0].name").value("secret author #1"))
                .andExpect(jsonPath("$.list[1].name").value("secret author #2"))
                .andExpect(jsonPath("$.list[2].name").value("secret author #3"))
                .andExpect(jsonPath("$.list[3].name").value("secret author #4"))
                .andExpect(jsonPath("$.list[4].name").value("secret author #5"));

        mvc.perform(post("/api/author/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "secret",
                          "page": "1"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(4))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.list[0].name").value("secret author #6"))
                .andExpect(jsonPath("$.list[1].name").value("secret author #7"))
                .andExpect(jsonPath("$.list[2].name").value("secret author #8"))
                .andExpect(jsonPath("$.list[3].name").value("secret author #9"));

        mvc.perform(post("/api/author/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "secret",
                          "size": "10"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(9))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.list[0].name").value("secret author #1"))
                .andExpect(jsonPath("$.list[1].name").value("secret author #2"))
                .andExpect(jsonPath("$.list[2].name").value("secret author #3"))
                .andExpect(jsonPath("$.list[3].name").value("secret author #4"))
                .andExpect(jsonPath("$.list[4].name").value("secret author #5"))
                .andExpect(jsonPath("$.list[5].name").value("secret author #6"))
                .andExpect(jsonPath("$.list[6].name").value("secret author #7"))
                .andExpect(jsonPath("$.list[7].name").value("secret author #8"))
                .andExpect(jsonPath("$.list[8].name").value("secret author #9"));


        mvc.perform(post("/api/author/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "secret",
                          "size": "2"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(2))
                .andExpect(jsonPath("$.totalPages").value(5))
                .andExpect(jsonPath("$.list[0].name").value("secret author #1"));

        mvc.perform(post("/api/author/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "secret",
                          "size": "2",
                          "page": "4"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list.length()").value(1))
                .andExpect(jsonPath("$.totalPages").value(5))
                .andExpect(jsonPath("$.list[0].name").value("secret author #9"));

    }
}