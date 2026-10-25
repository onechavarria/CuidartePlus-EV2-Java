package com.cuidarteplus.usuarios;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository repo;
    private final JwtService tokens;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public UsuarioService(UsuarioRepository repo, JwtService tokens) { this.repo = repo; this.tokens = tokens; }
    public Map<String, Object> registrar(String nombre, String correo, String password) {
        long id = repo.crear(nombre, correo, encoder.encode(password));
        return Map.of("id", id, "nombre_usuario", nombre, "correo", correo);
    }
    public Map<String, String> login(String nombre, String password) {
        var usuario = repo.porNombre(nombre)
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));
        if (!encoder.matches(password, usuario.contrasena())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }
        return Map.of("token", tokens.crear(usuario.id()), "token_type", "bearer");
    }
    public Map<String, Object> obtener(long id) {
        var u = repo.porId(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return Map.of("id", u.id(), "nombre_usuario", u.nombreUsuario(), "correo", u.correo());
    }
}
