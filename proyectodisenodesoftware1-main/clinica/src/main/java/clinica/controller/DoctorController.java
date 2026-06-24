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

@RestController
@RequestMapping("/api/doctores")
@CrossOrigin
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public List<DoctorResumenDTO> listar() {
        return doctorService.listar();
    }

    @GetMapping("/me")
    public DoctorPerfilDTO obtenerMe(Authentication authentication) {
        return doctorService.buscarPorUsername(authentication.getName());
    }

    @PutMapping("/me")
    public DoctorPerfilDTO actualizarMe(Authentication authentication,
                                        @Valid @RequestBody DoctorRequestDTO dto) {
        return doctorService.actualizarPorUsername(authentication.getName(), dto);
    }

    @GetMapping("/{id}")
    public DoctorPerfilDTO obtener(@PathVariable Long id) {
        return doctorService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorPerfilDTO crear(@Valid @RequestBody DoctorRequestDTO dto) {
        return doctorService.crear(dto);
    }

    @PutMapping("/{id}")
    public DoctorPerfilDTO actualizar(@PathVariable Long id,
                                      @Valid @RequestBody DoctorRequestDTO dto) {
        return doctorService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        doctorService.eliminar(id);
    }

    @GetMapping("/{id}/especialidades")
    public List<EspecialidadResponseDTO> obtenerEspecialidades(@PathVariable Long id) {
        return doctorService.especialidadesPorDoctor(id);
    }

    @PutMapping("/{id}/especialidades")
    public DoctorPerfilDTO actualizarEspecialidades(@PathVariable Long id,
                                                    @RequestBody List<Long> especialidadIds) {
        return doctorService.actualizarEspecialidades(id, especialidadIds);
    }
}