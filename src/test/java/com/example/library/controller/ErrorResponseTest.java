package com.example.library.controller;
import com.example.library.exception.GlobalExceptionHandler.ErrorResponse;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {
    @Test void validationFieldsAreDefensivelyCopiedAndImmutable() {
        var fields = new HashMap<String,String>();
        fields.put("name", "must not be blank");
        ErrorResponse response = new ErrorResponse("Validation failed", Instant.now(), fields);
        fields.put("name", "changed");
        assertEquals("must not be blank", response.fields().get("name"));
        assertThrows(UnsupportedOperationException.class, () -> response.fields().put("new", "value"));
    }
}
