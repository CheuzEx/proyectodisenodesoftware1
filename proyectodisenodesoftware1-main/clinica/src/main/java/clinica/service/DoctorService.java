package clinica.service;

import clinica.dto.doctor.DoctorPerfilDTO;
import clinica.dto.doctor.DoctorRequestDTO;
import clinica.dto.doctor.DoctorResumenDTO;
import clinica.dto.especialidad.EspecialidadResponseDTO;
import clinica.model.Doctor;
import clinica.model.Especialidad;
import clinica.model.Usuario;
import clinica.repository.CitaRepository;
import clinica.repository.DoctorRepository;
import clinica.repository.EspecialidadRepository;
import clinica.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepo;
    private final EspecialidadRepository especialidadRepo;
    private final UsuarioRepository usuarioRepo;
    private final CitaRepository citaRepo;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepo,
                         EspecialidadRepository especialidadRepo,
                         UsuarioRepository usuarioRepo,
                         CitaRepository citaRepo,
                         PasswordEncoder passwordEncoder) {
        this.doctorRepo = doctorRepo;
        this.especialidadRepo = especialidadRepo;
        this.usuarioRepo = usuarioRepo;
        this.citaRepo = citaRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<DoctorResumenDTO> listar() {
        return doctorRepo.findAll()
                .stream()
                .map(DoctorResumenDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DoctorPerfilDTO buscarPorId(Long id) {
        return DoctorPerfilDTO.fromEntity(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public DoctorPerfilDTO buscarPorUsername(String username) {
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado: " + username));

        if (usuario.getDoctor() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Este usuario no tiene un doctor asociado.");
        }

        return DoctorPerfilDTO.fromEntity(usuario.getDoctor());
    }

    @Transactional
    public DoctorPerfilDTO actualizarPorUsername(String username, DoctorRequestDTO dto) {
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado: " + username));

        if (usuario.getDoctor() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Este usuario no tiene un doctor asociado.");
        }

        Doctor doctor = usuario.getDoctor();
        mapearDesdeDTO(dto, doctor);

        return DoctorPerfilDTO.fromEntity(doctorRepo.save(doctor));
    }

    @Transactional
    public DoctorPerfilDTO crear(DoctorRequestDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El username es obligatorio para crear un doctor.");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La contraseña es obligatoria para crear un doctor.");
        }

        if (usuarioRepo.findByUsername(dto.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un usuario con el username: " + dto.getUsername());
        }

        Doctor doctor = new Doctor();
        mapearDesdeDTO(dto, doctor);

        Doctor doctorGuardado = doctorRepo.save(doctor);

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol("ROLE_DOCTOR");
        usuario.setDoctor(doctorGuardado);

        usuarioRepo.save(usuario);

        return DoctorPerfilDTO.fromEntity(doctorGuardado);
    }

    @Transactional
    public DoctorPerfilDTO actualizar(Long id, DoctorRequestDTO dto) {
        Doctor doctor = obtenerEntidad(id);
        mapearDesdeDTO(dto, doctor);

        return DoctorPerfilDTO.fromEntity(doctorRepo.save(doctor));
    }

    @Transactional
    public void eliminar(Long id) {
        obtenerEntidad(id);

        if (citaRepo.existsByDoctor_IdDoctor(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar el doctor porque tiene citas registradas.");
        }

        usuarioRepo.findByDoctor_IdDoctor(id).ifPresent(usuarioRepo::delete);

        doctorRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EspecialidadResponseDTO> especialidadesPorDoctor(Long doctorId) {
        return especialidadRepo.findByDoctores_IdDoctor(doctorId)
                .stream()
                .map(EspecialidadResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public DoctorPerfilDTO actualizarEspecialidades(Long doctorId, List<Long> especialidadIds) {
        Doctor doctor = obtenerEntidad(doctorId);

        Set<Especialidad> nuevas = new HashSet<>();

        if (especialidadIds != null && !especialidadIds.isEmpty()) {
            nuevas.addAll(especialidadRepo.findAllById(especialidadIds));
        }

        doctor.setEspecialidades(nuevas);

        return DoctorPerfilDTO.fromEntity(doctorRepo.save(doctor));
    }

    public Doctor obtenerEntidad(Long id) {
        return doctorRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Doctor no encontrado: " + id));
    }

    private void mapearDesdeDTO(DoctorRequestDTO dto, Doctor doctor) {
        doctor.setNombre(dto.getNombre());
        doctor.setApellido(dto.getApellido());
        doctor.setTelefono(dto.getTelefono());
        doctor.setDireccion(dto.getDireccion());
    }
}