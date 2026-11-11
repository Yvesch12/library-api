package com.example.library;

import com.example.library.entity.Borrowing;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingRepository;
import com.example.library.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LibraryApiEndpointsTests {
    private static final String BOOK_JSON = """
            {"isbn":"978-0-00-000001-1","title":"Test Book","author":"Test Author",
             "genre":"Programming","publishedYear":2020,"available":true}
            """;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BorrowingRepository borrowingRepository;

    @Test
    void listsExactlyFiveSeedBooksAndThreeSeedMembers() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(5)));
        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void supportsTheCompleteBookCrudLifecycle() throws Exception {
        String body = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(get("/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
        mockMvc.perform(put("/books/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(BOOK_JSON.replace("Test Book", "Updated Book").replace("true", "false")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Updated Book"))
                .andExpect(jsonPath("$.available").value(false));
        mockMvc.perform(get("/books/{id}", id))
                .andExpect(jsonPath("$.title").value("Updated Book"));
        mockMvc.perform(delete("/books/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/books/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void createsAndRetrievesMemberWithDefaults() throws Exception {
        String body = mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test Member","email":"test@usj.edu.lb"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.membershipDate").value(LocalDate.now().toString()))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(body).get("id").asLong();
        mockMvc.perform(get("/members/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@usj.edu.lb"));
    }

    @Test
    void returns404ForMissingResourcesAndDoesNotUpsertOnPut() throws Exception {
        mockMvc.perform(get("/books/999999")).andExpect(status().isNotFound());
        mockMvc.perform(get("/members/999999")).andExpect(status().isNotFound());
        mockMvc.perform(put("/books/999999").contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/books/999999")).andExpect(status().isNotFound());
        assertEquals(5, bookRepository.count());
    }

    @Test
    void rejectsInvalidBookAndMemberRequests() throws Exception {
        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON)
                        .content(BOOK_JSON.replace("Test Book", " ")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/books/1").contentType(MediaType.APPLICATION_JSON)
                        .content(BOOK_JSON.replace("2020", "0")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test Member","email":"invalid"}
                                """))
                .andExpect(status().isBadRequest());
        assertEquals(5, bookRepository.count());
        assertEquals(3, memberRepository.count());
    }

    @Test
    void suppliedIdCannotOverwriteAnExistingBookOnPost() throws Exception {
        JsonNode input = objectMapper.readTree(BOOK_JSON);
        ((com.fasterxml.jackson.databind.node.ObjectNode) input).put("id", 1);
        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());
        assertEquals("Clean Code", bookRepository.findById(1L).orElseThrow().getTitle());
        assertEquals(6, bookRepository.count());
    }

    @Test
    void persistsBorrowingRelationshipsAndExactDecimalFine() {
        Borrowing borrowing = new Borrowing(null, bookRepository.findById(1L).orElseThrow(),
                memberRepository.findById(1L).orElseThrow(), LocalDate.now(),
                LocalDate.now().plusDays(14), null, new BigDecimal("1.50"));
        Borrowing saved = borrowingRepository.saveAndFlush(borrowing);
        assertNotNull(saved.getId());
        Borrowing loaded = borrowingRepository.findById(saved.getId()).orElseThrow();
        assertEquals(1L, loaded.getBook().getId());
        assertEquals(1L, loaded.getMember().getId());
        assertEquals(0, new BigDecimal("1.50").compareTo(loaded.getFineAmount()));
        assertNull(loaded.getReturnDate());
    }

    @Test
    void openApiDescribesAllRequiredRoutesAndSwaggerUiIsServed() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/books'].get").exists())
                .andExpect(jsonPath("$.paths['/books'].post").exists())
                .andExpect(jsonPath("$.paths['/books/{id}'].get").exists())
                .andExpect(jsonPath("$.paths['/books/{id}'].put").exists())
                .andExpect(jsonPath("$.paths['/books/{id}'].delete").exists())
                .andExpect(jsonPath("$.paths['/members'].get").exists())
                .andExpect(jsonPath("$.paths['/members'].post").exists())
                .andExpect(jsonPath("$.paths['/members/{id}'].get").exists());
        mockMvc.perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
