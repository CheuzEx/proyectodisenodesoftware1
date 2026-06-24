package clinica.controller;

import clinica.dto.especialidad.EspecialidadRequestDTO;
import clinica.dto.especialidad.EspecialidadResponseDTO;
import clinica.service.EspecialidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@CrossOrigin
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    // GET /api/especialidades
    // Lista todas las especialidades ordenadas alfabéticamente.
    @GetMapping
    public List<EspecialidadResponseDTO> listar() {
        return especialidadService.listar();
    }

    // GET /api/especialidades/{id}
    // Obtiene una especialidad por ID.
    @GetMapping("/{id}")
    public EspecialidadResponseDTO obtener(@PathVariable Long id) {
        return especialidadService.buscarPorId(id);
    }

    // POST /api/especialidades
    // Crea una nueva especialidad. Devuelve 409 si el nombre ya existe.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EspecialidadResponseDTO crear(@Valid @RequestBody EspecialidadRequestDTO dto) {
        return especialidadService.crear(dto);
    }

    // PUT /api/especialidades/{id}
    // Actualiza el nombre de una especialidad existente.
    @PutMapping("/{id}")
    public EspecialidadResponseDTO actualizar(@PathVariable Long id,
                                              @Valid @RequestBody EspecialidadRequestDTO dto) {
        return especialidadService.actualizar(id, dto);
    }

    // DELETE /api/especialidades/{id}
    // Elimina una especialidad por ID.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        especialidadService.eliminar(id);
    }
}
