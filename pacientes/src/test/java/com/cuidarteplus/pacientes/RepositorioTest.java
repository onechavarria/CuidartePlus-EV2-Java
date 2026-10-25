package com.cuidarteplus.pacientes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

class RepositorioTest {
    PacienteRepository repo;
    @BeforeEach void setup() {
        var ds = new DriverManagerDataSource("jdbc:h2:mem:pacientes_" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        JdbcTemplate jdbc = new JdbcTemplate(ds);
        jdbc.execute("CREATE TABLE pacientes (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, usuario_id BIGINT, nombre VARCHAR(120), rut VARCHAR(20), telefono VARCHAR(30))");
        repo = new PacienteRepository(jdbc);
    }
    @Test void flujoCrud() {
        assertTrue(repo.listar(7).isEmpty());
        var paciente = repo.crear(7, "Ana", "123", "555");
        assertTrue(paciente.id() > 0);
        assertEquals(1, repo.listar(7).size());
        assertEquals("Ana", repo.obtener(paciente.id(), 7).orElseThrow().nombre());
        assertTrue(repo.obtener(paciente.id(), 8).isEmpty());
        var cambio = repo.actualizar(paciente.id(), 7, "Ana Maria", null, "999").orElseThrow();
        assertEquals("Ana Maria", cambio.nombre());
        assertFalse(repo.actualizar(999, 7, "X", null, null).isPresent());
        assertFalse(repo.eliminar(paciente.id(), 8));
        assertTrue(repo.eliminar(paciente.id(), 7));
        assertTrue(repo.listar(7).isEmpty());
    }
}
