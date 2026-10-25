package com.cuidarteplus.usuarios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UsuariosTest {
    UsuarioRepository repo;
    JwtService jwt;
    UsuarioService service;
    UsuariosController controller;
    JdbcTemplate jdbc;

    @BeforeEach void setup() {
        repo = mock(UsuarioRepository.class);
        jwt = new JwtService("abcdefghijklmnopqrstuvwxyzABCDEF1234567890");
        service = new UsuarioService(repo, jwt);
        jdbc = mock(JdbcTemplate.class);
        controller = new UsuariosController(service, jdbc);
    }
    @Test void registro() {
        when(repo.crear(eq("ana"), eq("ana@example.com"), anyString())).thenReturn(7L);
        var u = controller.registro(new UsuariosController.Registro("ana", "ana@example.com", "12345678"));
        assertEquals(7L, u.get("id")); assertEquals("ana", u.get("nombre_usuario"));
        verify(repo).crear(eq("ana"), eq("ana@example.com"), argThat(s -> s.startsWith("$2")));
    }
    @Test void loginCorrecto() {
        String hash = new BCryptPasswordEncoder().encode("clave-segura");
        when(repo.porNombre("ana")).thenReturn(Optional.of(new UsuarioRepository.Usuario(7,"ana","ana@example.com", hash)));
        var res = controller.login(new UsuariosController.Login("ana", "clave-segura"));
        assertEquals("bearer", res.get("token_type")); assertFalse(res.get("token").isEmpty());
    }
    @Test void loginIncorrecto() {
        String hash = new BCryptPasswordEncoder().encode("correcta");
        when(repo.porNombre("ana")).thenReturn(Optional.of(new UsuarioRepository.Usuario(7,"ana","ana@example.com", hash)));
        assertEquals(HttpStatus.UNAUTHORIZED, assertThrows(ApiException.class, () -> service.login("ana", "incorrecta")).getStatus());
    }
    @Test void usuarioInexistenteEnLogin() {
        when(repo.porNombre("nadie")).thenReturn(Optional.empty());
        assertEquals(HttpStatus.UNAUTHORIZED, assertThrows(ApiException.class, () -> service.login("nadie", "clave")).getStatus());
    }
    @Test void obtenerUsuario() {
        when(repo.porId(7)).thenReturn(Optional.of(new UsuarioRepository.Usuario(7,"ana","ana@example.com","hash")));
        assertEquals("ana", controller.usuario(7).get("nombre_usuario"));
        assertFalse(controller.usuario(7).containsKey("contrasena"));
    }
    @Test void usuarioInexistente() {
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ApiException.class, () -> controller.usuario(999)).getStatus());
    }
    @Test void health() {
        assertEquals("ok", controller.health().get("status"));
        verify(jdbc).queryForObject("SELECT 1", Integer.class);
    }
    @Test void jwtCorto() {
        assertThrows(IllegalArgumentException.class, () -> new JwtService("corto"));
    }
}
