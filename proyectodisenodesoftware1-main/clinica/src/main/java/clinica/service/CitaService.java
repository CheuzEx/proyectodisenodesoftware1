package clinica.service;

import clinica.dto.cita.CitaResponseDTO;
import clinica.dto.cita.CitaRequestDTO;
import clinica.model.Cita;
import clinica.model.Doctor;
import clinica.model.Especialidad;
import clinica.model.Paciente;
import clinica.repository.CitaRepository;
import clinica.repository.DoctorRepository;
import clinica.repository.EspecialidadRepository;
import clinica.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class CitaService {

    private final CitaRepository citaRepo;
    private final PacienteRepository pacienteRepo;
    private final EspecialidadRepository especialidadRepo;
    private final DoctorRepository doctorRepo;

    public CitaService(CitaRepository citaRepo,
                       PacienteRepository pacienteRepo,
                       EspecialidadRepository especialidadRepo,
                       DoctorRepository doctorRepo) {
        this.citaRepo = citaRepo;
        this.pacienteRepo = pacienteRepo;
        this.especialidadRepo = especialidadRepo;
        this.doctorRepo = doctorRepo;
    }

    // Devuelve las citas del doctor en el día.
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listarPorDia(Long doctorId, LocalDate fecha) {
        LocalDateTime start = fecha.atStartOfDay();
        LocalDateTime end   = fecha.atTime(LocalTime.MAX);
        return citaRepo.findByDoctor_IdDoctorAndFechaHoraBetweenOrderByFechaHoraAsc(doctorId, start, end)
                .stream()
                .map(CitaResponseDTO::fromEntity)
                .toList();
    }

    // Recibe un RequestDTO unificado y retorna un ResponseDTO.
    // Lanza 409 si el horario ya está ocupado o atendido.
    @Transactional
    public CitaResponseDTO crear(CitaRequestDTO dto) {
        LocalDateTime fechaHora = LocalDateTime.of(
                LocalDate.parse(dto.getFecha()), LocalTime.parse(dto.getHora()));

        List<Cita> citasEnEseHorario =
                citaRepo.findByDoctor_IdDoctorAndFechaHora(dto.getDoctorId(), fechaHora);

        boolean atendida   = citasEnEseHorario.stream()
                .anyMatch(c -> c.getEstado() == Cita.CitaEstado.ATENDIDA);
        boolean programada = citasEnEseHorario.stream()
                .anyMatch(c -> c.getEstado() == Cita.CitaEstado.PROGRAMADA);



        if (atendida) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "HORARIO_ATENDIDO: Ya se atendió una cita a esta hora. Elija otra hora.");
        }
        if (programada) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "HORARIO_OCUPADO: Ese horario ya está ocupado por una cita activa.");
        }

        Paciente paciente = pacienteRepo.findById(dto.getPacienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Paciente no existe: " + dto.getPacienteId()));

        Especialidad especialidad = especialidadRepo.findById(dto.getEspecialidadId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Especialidad no existe: " + dto.getEspecialidadId()));

        Doctor doctor = doctorRepo.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Doctor no existe: " + dto.getDoctorId()));

        Cita cita = new Cita();
        cita.setFechaHora(fechaHora);
        cita.setMotivo(dto.getMotivo());
        cita.setPaciente(paciente);
        cita.setDoctor(doctor);
        cita.setEspecialidad(especialidad);
        cita.setEstado(Cita.CitaEstado.PROGRAMADA);

        Cita guardada = citaRepo.save(cita);
        return CitaResponseDTO.fromEntity(guardada);
    }

    // Cancela una cita existente.
    @Transactional
    public CitaResponseDTO cancelar(Long id) {
        Cita cita = buscarEntidadPorId(id);
        cita.setEstado(Cita.CitaEstado.CANCELADA);
        return CitaResponseDTO.fromEntity(citaRepo.save(cita));
    }

    // Marca una cita como atendida.
    @Transactional
    public CitaResponseDTO atender(Long id) {
        Cita cita = buscarEntidadPorId(id);
        cita.setEstado(Cita.CitaEstado.ATENDIDA);
        return CitaResponseDTO.fromEntity(citaRepo.save(cita));
    }

    // Elimina una cita cancelada del registro del doctor.
    // No permite eliminar citas PROGRAMADAS (deben cancelarse primero).
    @Transactional
    public void eliminar(Long id) {
        Cita cita = buscarEntidadPorId(id);
        if (cita.getEstado() == Cita.CitaEstado.PROGRAMADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar una cita programada. Cancélela primero.");
        }
        citaRepo.deleteById(id);
    }

    // Verifica si un horario está disponible para un doctor.
    @Transactional(readOnly = true)
    public Map<String, Object> verificarDisponibilidad(Long doctorId, String fecha, String hora) {
        LocalDateTime fechaHora = LocalDateTime.of(
                LocalDate.parse(fecha), LocalTime.parse(hora));

        boolean ocupado = citaRepo.existsByDoctor_IdDoctorAndEstadoNotAndFechaHora(
                doctorId, Cita.CitaEstado.CANCELADA, fechaHora);

        return Map.of(
                "doctorId",   doctorId,
                "fechaHora",  fechaHora.toString(),
                "disponible", !ocupado,
                "mensaje",    ocupado
                        ? "Ya existe una cita activa a esa hora."
                        : "Horario disponible."
        );
    }

    // Devuelve todas las citas del día (para vista de admin).
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listarTodasPorDia(LocalDate fecha) {
        LocalDateTime start = fecha.atStartOfDay();
        LocalDateTime end   = fecha.atTime(LocalTime.MAX);
        return citaRepo.findByFechaHoraBetweenOrderByFechaHoraAsc(start, end).stream()
                .map(CitaResponseDTO::fromEntity)
                .toList();
    }

    // Helper interno: obtiene la entidad pura de la BD.
    private Cita buscarEntidadPorId(Long id) {
        return citaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cita no encontrada: " + id));
    }
}