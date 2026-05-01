package edu.itcr.clinica.controller;

import edu.itcr.clinica.model.Cita;
import edu.itcr.clinica.service.CitaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    /** DTO mínimo para crear una cita. */
    public static class CrearCitaRequest {
        public String fecha;
        public String hora;
        public String motivo;
        public Long   pacienteId;
        public Long   doctorId;
        public Long   especialidadId;
    }

    /** Devuelve las citas de un doctor en un día específico. */
    @GetMapping("/dia")
    public List<Cita> listarPorDia(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return citaService.listarPorDia(doctorId, fecha);
    }

    /** Crea una nueva cita, validando disponibilidad de horario. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cita crear(@RequestBody CrearCitaRequest req) {
        if (req.doctorId == null || req.pacienteId == null
                || req.fecha == null || req.hora == null || req.especialidadId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "doctorId, pacienteId, fecha, hora y especialidadId son obligatorios.");
        }
        return citaService.crear(
                req.doctorId, req.pacienteId, req.especialidadId,
                req.fecha, req.hora, req.motivo);
    }

    /** Cancela una cita existente. */
    @PatchMapping("/{id}/cancelar")
    public Cita cancelar(@PathVariable Long id) {
        return citaService.cancelar(id);
    }

    /** Marca una cita como atendida. */
    @PatchMapping("/{id}/atender")
    public Cita atender(@PathVariable Long id) {
        return citaService.atender(id);
    }

    /** Verifica si un horario está disponible para un doctor. */
    @GetMapping("/disponible")
    public Map<String, Object> verificarDisponibilidad(
            @RequestParam Long   doctorId,
            @RequestParam String fecha,
            @RequestParam String hora
    ) {
        return citaService.verificarDisponibilidad(doctorId, fecha, hora);
    }
}
