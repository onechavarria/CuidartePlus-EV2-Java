package com.cuidarteplus.pacientes;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PacienteService {
    private final PacienteRepository repo;
    public PacienteService(PacienteRepository repo) { this.repo = repo; }
    public List<PacienteRepository.Paciente> listar(long userId) { return repo.listar(userId); }
    public PacienteRepository.Paciente obtener(long id, long userId) {
        return repo.obtener(id, userId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Paciente no encontrado"));
    }
    public PacienteRepository.Paciente crear(long usuarioId, long autenticado, String nombre, String rut, String telefono) {
        if (usuarioId != autenticado) throw new ApiException(HttpStatus.FORBIDDEN, "No puedes crear pacientes de otro usuario");
        return repo.crear(usuarioId, nombre, rut, telefono);
    }
    public PacienteRepository.Paciente actualizar(long id, long userId, String nombre, String rut, String telefono) {
        return repo.actualizar(id, userId, nombre, rut, telefono)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Paciente no encontrado"));
    }
    public Map<String, Object> eliminar(long id, long userId) {
        if (!repo.eliminar(id, userId)) throw new ApiException(HttpStatus.NOT_FOUND, "Paciente no encontrado");
        return Map.of("mensaje", "Paciente eliminado", "id", id);
    }
}
