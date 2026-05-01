package edu.itcr.clinica.controller;

import edu.itcr.clinica.model.Especialidad;
import edu.itcr.clinica.service.EspecialidadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST para la entidad Especialidad.
 * Base path: /api/especialidades
 */
@RestController
@RequestMapping("/api/especialidades")
@CrossOrigin
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    /** DTO para recibir el nombre en el body. */
    public static class EspecialidadRequest {
        public String nombre;
    }

    // -----------------------------------------------------------------------
    // CRUD de Especialidad
    // -----------------------------------------------------------------------

    /**
     * GET /api/especialidades
     * Lista todas las especialidades ordenadas alfabéticamente.
     */
    @GetMapping
    public List<Especialidad> listar() {
        return especialidadService.listar();
    }

    /**
     * GET /api/especialidades/{id}
     * Obtiene una especialidad por ID.
     */
    @GetMapping("/{id}")
    public Especialidad obtener(@PathVariable Long id) {
        return especialidadService.buscarPorId(id);
    }

    /**
     * POST /api/especialidades
     * Crea una nueva especialidad. Devuelve 409 si el nombre ya existe.
     *
     * Body: { "nombre": "Cardiología" }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Especialidad crear(@RequestBody EspecialidadRequest req) {
        return especialidadService.crear(req.nombre);
    }

    /**
     * PUT /api/especialidades/{id}
     * Actualiza el nombre de una especialidad existente.
     *
     * Body: { "nombre": "Cardiología Pediátrica" }
     */
    @PutMapping("/{id}")
    public Especialidad actualizar(@PathVariable Long id,
                                   @RequestBody EspecialidadRequest req) {
        return especialidadService.actualizar(id, req.nombre);
    }

    /**
     * DELETE /api/especialidades/{id}
     * Elimina una especialidad por ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        especialidadService.eliminar(id);
    }
}
