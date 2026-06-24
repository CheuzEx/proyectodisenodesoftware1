package clinica.controller;

import clinica.dto.doctor.DoctorPerfilDTO;
import clinica.dto.doctor.DoctorRequestDTO;
import clinica.dto.doctor.DoctorResumenDTO;
import clinica.dto.especialidad.EspecialidadResponseDTO;
import clinica.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar operaciones relacionadas con médicos.
 * Expone endpoints para listar, crear, actualizar, eliminar y consultar
 * especialidades de los doctores.
 */
@RestController
@RequestMapping("/api/doctores")
@CrossOrigin
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // Obtiene la lista de todos los doctores (resumen)
    @GetMapping
    public List<DoctorResumenDTO> listar() {
        return doctorService.listar();
    }

    // Obtiene el perfil completo del doctor autenticado (según su username)
    @GetMapping("/me")
    public DoctorPerfilDTO obtenerMe(Authentication authentication) {
        return doctorService.buscarPorUsername(authentication.getName());
    }

    // Actualiza los datos del doctor autenticado
    @PutMapping("/me")
    public DoctorPerfilDTO actualizarMe(Authentication authentication,
                                        @Valid @RequestBody DoctorRequestDTO dto) {
        return doctorService.actualizarPorUsername(authentication.getName(), dto);
    }

    // Obtiene el perfil de un doctor por su ID
    @GetMapping("/{id}")
    public DoctorPerfilDTO obtener(@PathVariable Long id) {
        return doctorService.buscarPorId(id);
    }

    // Crea un nuevo doctor (solo administradores)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorPerfilDTO crear(@Valid @RequestBody DoctorRequestDTO dto) {
        return doctorService.crear(dto);
    }

    // Actualiza un doctor existente por su ID
    @PutMapping("/{id}")
    public DoctorPerfilDTO actualizar(@PathVariable Long id,
                                      @Valid @RequestBody DoctorRequestDTO dto) {
        return doctorService.actualizar(id, dto);
    }

    // Elimina un doctor por su ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        doctorService.eliminar(id);
    }

    // Obtiene la lista de especialidades de un doctor
    @GetMapping("/{id}/especialidades")
    public List<EspecialidadResponseDTO> obtenerEspecialidades(@PathVariable Long id) {
        return doctorService.especialidadesPorDoctor(id);
    }

    // Actualiza las especialidades de un doctor (reemplaza la lista completa)
    @PutMapping("/{id}/especialidades")
    public DoctorPerfilDTO actualizarEspecialidades(@PathVariable Long id,
                                                    @RequestBody List<Long> especialidadIds) {
        return doctorService.actualizarEspecialidades(id, especialidadIds);
    }
}
