package clinica.controller;

import clinica.dto.historial.HistorialResponseDTO;
import clinica.dto.historial.RegistrarAtencionRequestDTO;
import clinica.dto.paciente.PacienteResumenDTO;
import clinica.service.HistorialMedicoService;
import jakarta.validation.Valid;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historiales")
@CrossOrigin
public class HistorialMedicoController {

    private final HistorialMedicoService historialService;

    public HistorialMedicoController(HistorialMedicoService historialService) {
        this.historialService = historialService;
    }

    // GET /api/historiales
    // Lista todos los historiales médicos del sistema.
    @GetMapping
    public List<HistorialResponseDTO> listar() {
        return historialService.listar();
    }

    // GET /api/historiales/paciente/{idPaciente}
    // Devuelve los historiales de un paciente ordenados del más reciente al más antiguo.
    @GetMapping("/paciente/{idPaciente}")
    public List<HistorialResponseDTO> listarPorPaciente(@PathVariable Long idPaciente) {
        return historialService.listarPorPaciente(idPaciente);
    }

    // GET /api/historiales/cita/{idCita}
    // Busca el historial asociado a una cita específica.
    @GetMapping("/cita/{idCita}")
    public HistorialResponseDTO buscarPorCita(@PathVariable Long idCita) {
        return historialService.buscarPorCita(idCita);
    }

    // GET /api/historiales/paciente/{idPaciente}/datos
    // Devuelve los datos básicos del paciente para mostrar en la vista de historial.
    @GetMapping("/paciente/{idPaciente}/datos")
    public PacienteResumenDTO datosPaciente(@PathVariable Long idPaciente) {
        return PacienteResumenDTO.fromEntity(historialService.buscarPaciente(idPaciente));
    }

    @GetMapping("/paciente/{idPaciente}/citas-recetas")
    public List<Map<String, Object>> listarCitasConRecetas(@PathVariable Long idPaciente) {
        return historialService.listarCitasConRecetasPorPaciente(idPaciente);
    }

    // POST /api/historiales/atencion
    // Registra la atención de una cita: diagnóstico, tratamiento y recetas.
    @PostMapping("/atencion")
    @ResponseStatus(HttpStatus.CREATED)
    public HistorialResponseDTO registrarAtencion(
            @Valid @RequestBody RegistrarAtencionRequestDTO dto) {
        return historialService.registrarAtencion(dto);
    }
}
