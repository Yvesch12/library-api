package com.example.library.controller;
import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @Transactional
class ReservationControllerTest {
    @Autowired private MockMvc mvc;
    @Autowired private BookRepository books;
    @Autowired private ObjectMapper json;
    @Test void acceptanceCriteriaOverHttp() throws Exception {
        String request = "{\"bookId\":1,\"memberId\":1}";
        mvc.perform(post("/reservations").contentType("application/json").content(request))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.error").value("Book is available for direct borrowing."));
        Book b = books.findById(1L).orElseThrow(); b.setAvailable(false); books.saveAndFlush(b);
        String body = mvc.perform(post("/reservations").contentType("application/json").content(request))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("ACTIVE"))
            .andReturn().getResponse().getContentAsString();
        long id = json.readTree(body).get("id").asLong();
        mvc.perform(post("/reservations").contentType("application/json").content(request)).andExpect(status().isConflict());
        mvc.perform(put("/reservations/{id}/cancel", id)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CANCELLED"));
        mvc.perform(get("/members/1/reservations")).andExpect(status().isOk()).andExpect(jsonPath("$[0].status").value("CANCELLED"));
        mvc.perform(put("/reservations/{id}/cancel", id)).andExpect(status().isConflict());
    }
}
