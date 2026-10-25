package com.cuidarteplus.usuarios;

import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {
    private final JdbcTemplate jdbc;
    public UsuarioRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public long crear(String nombre, String correo, String hash) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO usuarios (nombre_usuario, correo, contrasena) VALUES (?,?,?)", new String[] {"id"});
            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, hash);
            return ps;
        }, holder);
        return holder.getKey().longValue();
    }
    public Optional<Usuario> porNombre(String nombre) {
        return jdbc.query("SELECT id, nombre_usuario, correo, contrasena FROM usuarios WHERE nombre_usuario=?",
            (rs, n) -> new Usuario(rs.getLong("id"), rs.getString("nombre_usuario"), rs.getString("correo"), rs.getString("contrasena")), nombre).stream().findFirst();
    }
    public Optional<Usuario> porId(long id) {
        return jdbc.query("SELECT id, nombre_usuario, correo, contrasena FROM usuarios WHERE id=?",
            (rs, n) -> new Usuario(rs.getLong("id"), rs.getString("nombre_usuario"), rs.getString("correo"), rs.getString("contrasena")), id).stream().findFirst();
    }
    public record Usuario(long id, String nombreUsuario, String correo, String contrasena) { }
}
