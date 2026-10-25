package com.cuidarteplus.usuarios;

import java.util.Map;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsuariosController {
    private final UsuarioService service;
    private final JdbcTemplate jdbc;
    public UsuariosController(UsuarioService service, JdbcTemplate jdbc) { this.service = service; this.jdbc = jdbc; }
    public record Registro(@NotBlank @Size(min=3, max=80) String nombre_usuario,
                           @NotBlank @Email String correo,
                           @NotBlank @Size(min=8, max=128) String password) { }
    public record Login(@NotBlank String nombre_usuario, @NotBlank String password) { }

    @GetMapping("/health")
    public Map<String, String> health() { jdbc.queryForObject("SELECT 1", Integer.class); return Map.of("status", "ok"); }
    @PostMapping("/autenticacion/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> registro(@Valid @RequestBody Registro body) {
        return service.registrar(body.nombre_usuario(), body.correo(), body.password());
    }
    @PostMapping("/autenticacion/login")
    public Map<String, String> login(@Valid @RequestBody Login body) {
        return service.login(body.nombre_usuario(), body.password());
    }
    @GetMapping("/usuarios/{usuarioId}")
    public Map<String, Object> usuario(@PathVariable long usuarioId) { return service.obtener(usuarioId); }
}
