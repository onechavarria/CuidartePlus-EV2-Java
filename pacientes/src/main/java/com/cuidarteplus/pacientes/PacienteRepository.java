package com.cuidarteplus.pacientes;

import java.util.List;
import java.util.Optional;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PacienteRepository {
    private final JdbcTemplate jdbc;
    public PacienteRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public record Paciente(long id, long usuario_id, String nombre, String rut, String telefono) { }
    private final org.springframework.jdbc.core.RowMapper<Paciente> mapper = (rs, n) ->
        new Paciente(rs.getLong("id"), rs.getLong("usuario_id"), rs.getString("nombre"), rs.getString("rut"), rs.getString("telefono"));

    public List<Paciente> listar(long usuarioId) {
        return jdbc.query("SELECT id, usuario_id, nombre, rut, telefono FROM pacientes WHERE usuario_id=? ORDER BY id", mapper, usuarioId);
    }
    public Optional<Paciente> obtener(long id, long usuarioId) {
        return jdbc.query("SELECT id, usuario_id, nombre, rut, telefono FROM pacientes WHERE id=? AND usuario_id=?", mapper, id, usuarioId).stream().findFirst();
    }
    public Paciente crear(long usuarioId, String nombre, String rut, String telefono) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO pacientes (usuario_id, nombre, rut, telefono) VALUES (?,?,?,?)", new String[] {"id"});
            ps.setLong(1, usuarioId); ps.setString(2, nombre); ps.setString(3, rut); ps.setString(4, telefono);
            return ps;
        }, holder);
        return obtener(holder.getKey().longValue(), usuarioId).orElseThrow();
    }
    public Optional<Paciente> actualizar(long id, long usuarioId, String nombre, String rut, String telefono) {
        int updated = jdbc.update("UPDATE pacientes SET nombre=?, rut=?, telefono=? WHERE id=? AND usuario_id=?",
            nombre, rut, telefono, id, usuarioId);
        return updated == 0 ? Optional.empty() : obtener(id, usuarioId);
    }
    public boolean eliminar(long id, long usuarioId) {
        return jdbc.update("DELETE FROM pacientes WHERE id=? AND usuario_id=?", id, usuarioId) > 0;
    }
}
