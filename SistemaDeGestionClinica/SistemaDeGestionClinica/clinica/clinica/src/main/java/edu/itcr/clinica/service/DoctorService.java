package edu.itcr.clinica.service;

import edu.itcr.clinica.model.Doctor;
import edu.itcr.clinica.model.Especialidad;
import edu.itcr.clinica.repository.DoctorRepository;
import edu.itcr.clinica.repository.EspecialidadRepository;
import org.springframework.http.HttpStatus;
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

    public DoctorService(DoctorRepository doctorRepo,
                         EspecialidadRepository especialidadRepo) {
        this.doctorRepo       = doctorRepo;
        this.especialidadRepo = especialidadRepo;
    }

    /** Devuelve todos los doctores registrados. */
    @Transactional(readOnly = true)
    public List<Doctor> listar() {
        return doctorRepo.findAll();
    }

    /** Busca un doctor por su ID, lanzando 404 si no existe. */
    @Transactional(readOnly = true)
    public Doctor buscarPorId(Long id) {
        return doctorRepo.findByIdDoctor(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Doctor no encontrado: " + id));
    }

    /** Persiste un nuevo doctor. */
    @Transactional
    public Doctor crear(Doctor doctor) {
        return doctorRepo.save(doctor);
    }

    /** Elimina un doctor por ID. */
    @Transactional
    public void eliminar(Long id) {
        doctorRepo.deleteById(id);
    }

    /** Actualiza los datos personales de un doctor existente. */
    @Transactional
    public Doctor actualizar(Long id, Doctor cambios) {
        Doctor doctor = buscarPorId(id);
        doctor.setNombre(cambios.getNombre());
        doctor.setApellido(cambios.getApellido());
        doctor.setTelefono(cambios.getTelefono());
        doctor.setDireccion(cambios.getDireccion());
        return doctorRepo.save(doctor);
    }

    /**
     * Reemplaza el conjunto de especialidades de un doctor.
     * Si la lista de IDs es nula o vacía, se eliminan todas las especialidades.
     */
    @Transactional
    public Doctor actualizarEspecialidades(Long doctorId, List<Long> especialidadIds) {
        Doctor doctor = buscarPorId(doctorId);
        Set<Especialidad> nuevas = new HashSet<>();
        if (especialidadIds != null && !especialidadIds.isEmpty()) {
            nuevas.addAll(especialidadRepo.findAllById(especialidadIds));
        }
        doctor.setEspecialidades(nuevas);
        return doctorRepo.save(doctor);
    }

    /**
     * Crea una especialidad nueva si no existe ya (por nombre, ignorando mayúsculas).
     * Devuelve la especialidad existente o la recién creada.
     */
    @Transactional
    public Especialidad crearEspecialidadSiNoExiste(String nombre) {
        String clean = nombre == null ? "" : nombre.trim();
        if (clean.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El nombre de la especialidad no puede estar vacío.");
        }
        return especialidadRepo
                .findByNomEspecialidadIgnoreCase(clean)
                .orElseGet(() -> especialidadRepo.save(new Especialidad(clean)));
    }

    /** Devuelve las especialidades de un doctor ordenadas alfabéticamente. */
    @Transactional(readOnly = true)
    public List<Especialidad> especialidadesPorDoctor(Long doctorId) {
        return especialidadRepo.findByDoctores_IdDoctor(doctorId);
    }

    /** Devuelve todas las especialidades registradas en el sistema. */
    @Transactional(readOnly = true)
    public List<Especialidad> listarEspecialidades() {
        return especialidadRepo.findAll();
    }
}
