package com.example.library.controller;
import com.example.library.service.BorrowingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @Transactional
class GlobalExceptionHandlerTest {
    @Autowired private MockMvc mvc;
    @Autowired private BorrowingService service;
    @Test void nonexistentBookReturns404WithTimestamp() throws Exception {
        mvc.perform(post("/borrowings").contentType("application/json").content("{\"bookId\":99999,\"memberId\":1}"))
            .andExpect(status().isNotFound()).andExpect(jsonPath("$.error").value("Book not found"))
            .andExpect(jsonPath("$.timestamp").exists());
    }
    @Test void alreadyBorrowedReturns409() throws Exception {
        service.borrowBook(1L, 1L);
        mvc.perform(post("/borrowings").contentType("application/json").content("{\"bookId\":1,\"memberId\":2}"))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.error").value("Book is already borrowed"));
    }
    @Test void validationAndMalformedJsonReturn400() throws Exception {
        mvc.perform(post("/borrowings").contentType("application/json").content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fields.bookId").exists());
        mvc.perform(post("/borrowings").contentType("application/json").content("{"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("Malformed request"));
    }
}
