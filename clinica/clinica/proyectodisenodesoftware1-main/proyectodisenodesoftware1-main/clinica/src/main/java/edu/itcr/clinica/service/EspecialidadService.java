package edu.itcr.clinica.service;

import edu.itcr.clinica.model.Especialidad;
import edu.itcr.clinica.repository.EspecialidadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepo;

    public EspecialidadService(EspecialidadRepository especialidadRepo) {
        this.especialidadRepo = especialidadRepo;
    }

    /** Devuelve todas las especialidades ordenadas por nombre. */
    @Transactional(readOnly = true)
    public List<Especialidad> listar() {
        return especialidadRepo.findAll().stream()
                .sorted((a, b) -> a.getNomEspecialidad()
                        .compareToIgnoreCase(b.getNomEspecialidad()))
                .toList();
    }

    /** Busca una especialidad por ID, lanzando 404 si no existe. */
    @Transactional(readOnly = true)
    public Especialidad buscarPorId(Long id) {
        return especialidadRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Especialidad no encontrada: " + id));
    }

    /**
     * Crea una especialidad nueva.
     * Lanza 409 CONFLICT si ya existe una con el mismo nombre (ignorando mayúsculas).
     */
    @Transactional
    public Especialidad crear(String nombre) {
        String clean = validarNombre(nombre);
        if (especialidadRepo.findByNomEspecialidadIgnoreCase(clean).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una especialidad con el nombre: " + clean);
        }
        return especialidadRepo.save(new Especialidad(clean));
    }

    /**
     * Actualiza el nombre de una especialidad existente.
     * Lanza 409 si el nuevo nombre ya está en uso por otra especialidad.
     */
    @Transactional
    public Especialidad actualizar(Long id, String nuevoNombre) {
        String clean        = validarNombre(nuevoNombre);
        Especialidad existente = buscarPorId(id);

        especialidadRepo.findByNomEspecialidadIgnoreCase(clean)
                .filter(e -> !e.getIdEspecialidad().equals(id))
                .ifPresent(e -> { throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ya existe otra especialidad con el nombre: " + clean); });

        existente.setNomEspecialidad(clean);
        return especialidadRepo.save(existente);
    }

    /** Elimina una especialidad por ID. */
    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id); // valida que exista antes de borrar
        especialidadRepo.deleteById(id);
    }

    // -----------------------------------------------------------------------
    // Helper interno
    // -----------------------------------------------------------------------

    private String validarNombre(String nombre) {
        String clean = nombre == null ? "" : nombre.trim();
        if (clean.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El nombre de la especialidad no puede estar vacío.");
        }
        return clean;
    }
}
