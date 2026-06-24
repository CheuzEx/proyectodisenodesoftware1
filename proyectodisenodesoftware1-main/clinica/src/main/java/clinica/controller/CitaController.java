package clinica.controller;

import clinica.dto.cita.CitaRequestDTO;
import clinica.dto.cita.CitaResponseDTO;
import clinica.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    // GET /api/citas/dia?doctorId=1&fecha=2026-06-14
    // Devuelve las citas de un doctor en un día específico.
    @GetMapping("/dia")
    public List<CitaResponseDTO> listarPorDia(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return citaService.listarPorDia(doctorId, fecha);
    }
    // GET /api/citas/todas?fecha=2026-06-15
// Devuelve todas las citas de ese día (solo ADMIN).
    @GetMapping("/todas")
    public List<CitaResponseDTO> listarTodasPorDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return citaService.listarTodasPorDia(fecha);
    }
    // POST /api/citas
    // Crea una nueva cita. Devuelve 409 si el horario está ocupado.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitaResponseDTO crear(@Valid @RequestBody CitaRequestDTO dto) {
        return citaService.crear(dto);
    }

    // PUT /api/citas/{id}/cancelar
    // Cancela una cita existente.
    @PutMapping("/{id}/cancelar")
    public CitaResponseDTO cancelar(@PathVariable Long id) {
        return citaService.cancelar(id);
    }

    // PUT /api/citas/{id}/atender
    // Marca una cita como atendida.
    @PutMapping("/{id}/atender")
    public CitaResponseDTO atender(@PathVariable Long id) {
        return citaService.atender(id);
    }

    // GET /api/citas/disponibilidad?doctorId=1&fecha=2026-06-14&hora=09:00
    // Verifica si un horario está disponible para un doctor.
    @GetMapping("/disponibilidad")
    public Map<String, Object> verificarDisponibilidad(
            @RequestParam Long doctorId,
            @RequestParam String fecha,
            @RequestParam String hora) {
        return citaService.verificarDisponibilidad(doctorId, fecha, hora);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
    }
}
