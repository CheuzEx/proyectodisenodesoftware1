package clinica.service;

import clinica.dto.especialidad.EspecialidadResponseDTO;
import clinica.dto.especialidad.EspecialidadRequestDTO;
import clinica.model.Especialidad;
import clinica.repository.EspecialidadRepository;
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

    // Devuelve una lista de EspecialidadResponseDTO ordenada por nombre
    @Transactional(readOnly = true)
    public List<EspecialidadResponseDTO> listar() {
        return especialidadRepo.findAll().stream()
                .sorted((a, b) -> a.getNomEspecialidad()
                        .compareToIgnoreCase(b.getNomEspecialidad()))
                .map(EspecialidadResponseDTO::fromEntity)
                .toList();
    }

    // Devuelve directamente el ResponseDTO utilizando su método fromEntity
    @Transactional(readOnly = true)
    public EspecialidadResponseDTO buscarPorId(Long id) {
        Especialidad esp = especialidadRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Especialidad no encontrada: " + id));
        return EspecialidadResponseDTO.fromEntity(esp);
    }

    // Helper interno para obtener la entidad pura cuando otros métodos del servicio la necesitan
    private Especialidad buscarEntidadPorId(Long id) {
        return especialidadRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Especialidad no encontrada: " + id));
    }

    // CORREGIDO: Lee la propiedad usando .getNombre() de tu RequestDTO
    @Transactional
    public EspecialidadResponseDTO crear(EspecialidadRequestDTO dto) {
        String clean = validarNombre(dto.getNombre());

        if (especialidadRepo.findByNomEspecialidadIgnoreCase(clean).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una especialidad con el nombre: " + clean);
        }

        Especialidad nueva = especialidadRepo.save(new Especialidad(clean));
        return EspecialidadResponseDTO.fromEntity(nueva);
    }

    // CORREGIDO: Lee la propiedad usando .getNombre() de tu RequestDTO
    @Transactional
    public EspecialidadResponseDTO actualizar(Long id, EspecialidadRequestDTO dto) {
        String clean = validarNombre(dto.getNombre());
        Especialidad existente = buscarEntidadPorId(id);

        especialidadRepo.findByNomEspecialidadIgnoreCase(clean)
                .filter(e -> !e.getIdEspecialidad().equals(id))
                .ifPresent(e -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Ya existe otra especialidad con el nombre: " + clean);
                });

        existente.setNomEspecialidad(clean);
        Especialidad actualizada = especialidadRepo.save(existente);
        return EspecialidadResponseDTO.fromEntity(actualizada);
    }

    // Elimina una especialidad por ID
    @Transactional
    public void eliminar(Long id) {
        buscarEntidadPorId(id); // Valida que exista antes de borrar
        especialidadRepo.deleteById(id);
    }

    // Helper interno para limpiar cadenas vacías
    private String validarNombre(String nombre) {
        String clean = nombre == null ? "" : nombre.trim();
        if (clean.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El nombre de la especialidad no puede estar vacío.");
        }
        return clean;
    }
}