package com.cuidarteplus.pacientes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

class PacientesTest {
    PacienteRepository repo;
    PacienteService service;
    JwtService jwt;
    PacientesController controller;
    JdbcTemplate jdbc;
    PacienteRepository.Paciente paciente = new PacienteRepository.Paciente(3, 7, "Ana", "123", "555");
    String token;
    @BeforeEach void setup() {
        repo = mock(PacienteRepository.class);
        service = new PacienteService(repo);
        jwt = new JwtService("abcdefghijklmnopqrstuvwxyzABCDEF1234567890");
        token = "Bearer " + io.jsonwebtoken.Jwts.builder().subject("7")
            .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("abcdefghijklmnopqrstuvwxyzABCDEF1234567890".getBytes(java.nio.charset.StandardCharsets.UTF_8))).compact();
        jdbc = mock(JdbcTemplate.class);
        controller = new PacientesController(service, jwt, jdbc);
    }
    @Test void autentica() { assertEquals(7, jwt.validar(token)); }
    @Test void sinToken() { assertEquals(HttpStatus.UNAUTHORIZED, assertThrows(ApiException.class, () -> jwt.validar(null)).getStatus()); }
    @Test void tokenInvalido() { assertEquals(HttpStatus.UNAUTHORIZED, assertThrows(ApiException.class, () -> jwt.validar("Bearer inventado")).getStatus()); }
    @Test void claveCorta() { assertThrows(IllegalArgumentException.class, () -> new JwtService("corta")); }
    @Test void listar() { when(repo.listar(7)).thenReturn(List.of(paciente)); assertEquals(1, controller.listar(token).size()); }
    @Test void obtener() { when(repo.obtener(3,7)).thenReturn(Optional.of(paciente)); assertEquals("Ana", controller.obtener(3,token).nombre()); }
    @Test void obtenerInexistente() { assertEquals(HttpStatus.NOT_FOUND, assertThrows(ApiException.class, () -> controller.obtener(300,token)).getStatus()); }
    @Test void crear() {
        when(repo.crear(7,"Ana","123","555")).thenReturn(paciente);
        assertEquals(3, controller.crear(new PacientesController.Nuevo(7,"Ana","123","555"), token).id());
    }
    @Test void prohibido() { assertEquals(HttpStatus.FORBIDDEN, assertThrows(ApiException.class,
        () -> controller.crear(new PacientesController.Nuevo(8,"Ana",null,null), token)).getStatus()); }
    @Test void actualizar() {
        when(repo.actualizar(3,7,"Ana",null,null)).thenReturn(Optional.of(paciente));
        assertEquals(3, controller.actualizar(3,new PacientesController.Cambio("Ana",null,null),token).id());
    }
    @Test void actualizarInexistente() { assertEquals(HttpStatus.NOT_FOUND, assertThrows(ApiException.class,
        () -> controller.actualizar(42,new PacientesController.Cambio("Ana",null,null),token)).getStatus()); }
    @Test void eliminar() { when(repo.eliminar(3,7)).thenReturn(true); assertEquals("Paciente eliminado",controller.eliminar(3,token).get("mensaje")); }
    @Test void eliminarInexistente() { assertEquals(HttpStatus.NOT_FOUND, assertThrows(ApiException.class, () -> controller.eliminar(42,token)).getStatus()); }
    @Test void health() { assertEquals("ok", controller.health().get("status")); verify(jdbc).queryForObject("SELECT 1",Integer.class); }
}
