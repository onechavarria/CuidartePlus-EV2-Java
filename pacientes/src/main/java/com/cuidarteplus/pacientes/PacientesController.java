package com.cuidarteplus.pacientes;

import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class PacientesController {
    private final PacienteService service;
    private final JwtService jwt;
    private final JdbcTemplate jdbc;
    public PacientesController(PacienteService service, JwtService jwt, JdbcTemplate jdbc) {
        this.service = service; this.jwt = jwt; this.jdbc = jdbc;
    }
    public record Nuevo(@Min(1) long usuario_id, @NotBlank @Size(min=2, max=120) String nombre,
                        @Size(max=20) String rut, @Size(max=30) String telefono) { }
    public record Cambio(@NotBlank @Size(min=2, max=120) String nombre,
                         @Size(max=20) String rut, @Size(max=30) String telefono) { }

    @GetMapping("/health")
    public Map<String, String> health() { jdbc.queryForObject("SELECT 1", Integer.class); return Map.of("status", "ok"); }
    @GetMapping("/pacientes")
    public List<PacienteRepository.Paciente> listar(@RequestHeader(value="Authorization", required=false) String auth) {
        return service.listar(jwt.validar(auth));
    }
    @GetMapping("/pacientes/{id}")
    public PacienteRepository.Paciente obtener(@PathVariable long id, @RequestHeader(value="Authorization", required=false) String auth) {
        return service.obtener(id, jwt.validar(auth));
    }
    @PostMapping("/pacientes")
    @ResponseStatus(HttpStatus.CREATED)
    public PacienteRepository.Paciente crear(@Valid @RequestBody Nuevo body, @RequestHeader(value="Authorization", required=false) String auth) {
        return service.crear(body.usuario_id(), jwt.validar(auth), body.nombre(), body.rut(), body.telefono());
    }
    @PutMapping("/pacientes/{id}")
    public PacienteRepository.Paciente actualizar(@PathVariable long id, @Valid @RequestBody Cambio body,
            @RequestHeader(value="Authorization", required=false) String auth) {
        return service.actualizar(id, jwt.validar(auth), body.nombre(), body.rut(), body.telefono());
    }
    @DeleteMapping("/pacientes/{id}")
    public Map<String, Object> eliminar(@PathVariable long id, @RequestHeader(value="Authorization", required=false) String auth) {
        return service.eliminar(id, jwt.validar(auth));
    }
}
