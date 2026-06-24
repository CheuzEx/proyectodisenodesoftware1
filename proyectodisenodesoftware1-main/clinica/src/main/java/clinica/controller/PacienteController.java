package clinica.controller;

import clinica.dto.paciente.PacienteDetalleDTO;
import clinica.dto.paciente.PacienteRequestDTO;
import clinica.dto.paciente.PacienteResumenDTO;
import clinica.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // GET /api/pacientes
    // Lista todos los pacientes como resumen ordenados por apellido.
    @GetMapping
    public List<PacienteResumenDTO> listar() {
        return pacienteService.listar();
    }

    // GET /api/pacientes/{id}
    // Obtiene el detalle completo de un paciente.
    @GetMapping("/{id}")
    public PacienteDetalleDTO obtener(@PathVariable Long id) {
        return pacienteService.buscarPorId(id);
    }

    // POST /api/pacientes
    // Crea un nuevo paciente con su historial médico inicial.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PacienteDetalleDTO crear(@Valid @RequestBody PacienteRequestDTO dto) {
        return pacienteService.crearConHistorial(dto);
    }

    // PUT /api/pacientes/{id}
    // Actualiza los datos de un paciente existente.
    @PutMapping("/{id}")
    public PacienteDetalleDTO actualizar(@PathVariable Long id,
                                         @Valid @RequestBody PacienteRequestDTO dto) {
        return pacienteService.actualizar(id, dto);
    }

    // DELETE /api/pacientes/{id}
    // Elimina un paciente por ID.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
    }
}
