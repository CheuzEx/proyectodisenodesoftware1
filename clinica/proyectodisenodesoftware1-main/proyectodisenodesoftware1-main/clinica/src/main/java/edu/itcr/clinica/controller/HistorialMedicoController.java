package edu.itcr.clinica.controller;

import edu.itcr.clinica.model.HistorialMedico;
import edu.itcr.clinica.service.HistorialMedicoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * API REST para la entidad HistorialMedico.
 * Base path: /historiales
 *
 * Las vistas Thymeleaf asociadas se encuentran en VistaController.
 */
@RestController
@RequestMapping("/api/historiales")
@CrossOrigin
public class HistorialMedicoController {

    private final HistorialMedicoService historialService;

    public HistorialMedicoController(HistorialMedicoService historialService) {
        this.historialService = historialService;
    }

    /** DTO público para recibir datos de receta desde el cliente. */
    public static class RecetaDTO {
        public String  medicamento;
        public String  dosis;
        public String  frecuencia;
        public Integer duracion;
    }

    /** DTO para registrar la atención de una cita. */
    public static class RegistrarAtencionRequest {
        public Long            citaId;
        public String          diagnostico;
        public String          tratamiento;
        public List<RecetaDTO> recetas;
        public LocalDate       fechaConsulta;
    }

    /** GET /historiales — Lista todos los historiales. */
    @GetMapping
    public List<HistorialMedico> listar() {
        return historialService.listar();
    }

    /** GET /historiales/por-paciente/{idPaciente} — Historiales de un paciente. */
    @GetMapping("/por-paciente/{idPaciente}")
    public List<HistorialMedico> listarPorPaciente(@PathVariable Long idPaciente) {
        return historialService.listarPorPaciente(idPaciente);
    }

    /** GET /historiales/por-cita/{idCita} — Historial asociado a una cita. */
    @GetMapping("/por-cita/{idCita}")
    public HistorialMedico buscarPorCita(@PathVariable Long idCita) {
        return historialService.buscarPorCita(idCita);
    }

    /** POST /historiales/registrar — Registra la atención de una cita con diagnóstico y recetas. */
    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public HistorialMedico registrar(@RequestBody RegistrarAtencionRequest req) {
        List<HistorialMedicoService.RecetaInput> recetasInput = null;
        if (req.recetas != null) {
            recetasInput = req.recetas.stream().map(dto -> {
                HistorialMedicoService.RecetaInput r = new HistorialMedicoService.RecetaInput();
                r.medicamento = dto.medicamento;
                r.dosis       = dto.dosis;
                r.frecuencia  = dto.frecuencia;
                r.duracion    = dto.duracion;
                return r;
            }).toList();
        }

        return historialService.registrarAtencion(
                req.citaId,
                req.diagnostico,
                req.tratamiento,
                req.fechaConsulta,
                recetasInput
        );
    }
}
