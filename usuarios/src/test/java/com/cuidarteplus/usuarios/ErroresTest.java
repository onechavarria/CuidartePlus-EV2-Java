package com.cuidarteplus.usuarios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DuplicateKeyException;

class ErroresTest {
    @Test void errorApi() {
        var advice = new ApiErrors();
        var response = advice.api(new ApiException(HttpStatus.NOT_FOUND, "No encontrado"));
        assertEquals(404, response.getStatusCode().value());
        assertEquals("No encontrado", response.getBody().get("detail"));
    }
    @Test void errorDeIntegridad() {
        var response = new ApiErrors().conflict(new DuplicateKeyException("duplicate"));
        assertEquals(409, response.getStatusCode().value());
    }
}
