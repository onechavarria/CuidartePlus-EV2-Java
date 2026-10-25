package com.cuidarteplus.usuarios;

import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

class RepositorioTest {
    UsuarioRepository repo;
    @BeforeEach void setup() {
        var ds = new DriverManagerDataSource("jdbc:h2:mem:usuarios_" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        JdbcTemplate jdbc = new JdbcTemplate(ds);
        jdbc.execute("CREATE TABLE usuarios (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, nombre_usuario VARCHAR(80) UNIQUE, correo VARCHAR(254) UNIQUE, contrasena VARCHAR(120))");
        repo = new UsuarioRepository(jdbc);
    }
    @Test void crearYConsultar() {
        long id = repo.crear("ana", "ana@example.com", "hash");
        assertTrue(id > 0);
        assertEquals("ana", repo.porNombre("ana").orElseThrow().nombreUsuario());
        assertEquals("ana@example.com", repo.porId(id).orElseThrow().correo());
    }
    @Test void ausentes() {
        assertTrue(repo.porNombre("nada").isEmpty());
        assertTrue(repo.porId(100).isEmpty());
    }
}
