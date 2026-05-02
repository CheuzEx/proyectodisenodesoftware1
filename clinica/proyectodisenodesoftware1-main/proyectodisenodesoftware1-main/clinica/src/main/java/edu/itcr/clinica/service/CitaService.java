package edu.itcr.clinica.service;

import edu.itcr.clinica.model.Cita;
import edu.itcr.clinica.model.Doctor;
import edu.itcr.clinica.model.Especialidad;
import edu.itcr.clinica.model.Paciente;
import edu.itcr.clinica.repository.CitaRepository;
import edu.itcr.clinica.repository.DoctorRepository;
import edu.itcr.clinica.repository.EspecialidadRepository;
import edu.itcr.clinica.repository.PacienteRepository;
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

    // Devuelve todas las citas de un doctor en un día específico. 
    @Transactional(readOnly = true)
    public List<Cita> listarPorDia(Long doctorId, LocalDate fecha) {
        LocalDateTime start = fecha.atStartOfDay();
        LocalDateTime end   = fecha.atTime(LocalTime.MAX);
        return citaRepo.findByDoctor_IdDoctorAndFechaHoraBetweenOrderByFechaHoraAsc(doctorId, start, end);
    }

    // Crea una nueva cita aplicando las reglas de negocio:
    // El horario no puede estar ya ocupado por una cita ATENDIDA o PROGRAMADA.
    // Paciente y Especialidad deben existir.
    @Transactional
    public Cita crear(Long doctorId, Long pacienteId, Long especialidadId,
                      String fecha, String hora, String motivo) {

        LocalDateTime fechaHora = LocalDateTime.of(
                LocalDate.parse(fecha), LocalTime.parse(hora));

        List<Cita> citasEnEseHorario =
                citaRepo.findByDoctor_IdDoctorAndFechaHora(doctorId, fechaHora);

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

        Paciente paciente = pacienteRepo.findById(pacienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Paciente no existe: " + pacienteId));

        Especialidad especialidad = especialidadRepo.findById(especialidadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Especialidad no existe: " + especialidadId));

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Doctor no existe: " + doctorId));

        Cita cita = new Cita();
        cita.setFechaHora(fechaHora);
        cita.setMotivo(motivo);
        cita.setPaciente(paciente);
        cita.setDoctor(doctor);
        cita.setEspecialidad(especialidad);
        cita.setEstado(Cita.CitaEstado.PROGRAMADA);

        return citaRepo.save(cita);
    }

    // Cambia el estado de una cita a CANCELADA. 
    @Transactional
    public Cita cancelar(Long id) {
        Cita cita = buscarPorId(id);
        cita.setEstado(Cita.CitaEstado.CANCELADA);
        return citaRepo.save(cita);
    }

    // Cambia el estado de una cita a ATENDIDA. 
    @Transactional
    public Cita atender(Long id) {
        Cita cita = buscarPorId(id);
        cita.setEstado(Cita.CitaEstado.ATENDIDA);
        return citaRepo.save(cita);
    }

    // Consulta si un horario está disponible para un doctor.
    // Un horario se considera libre cuando solo hay citas CANCELADAS (o ninguna).
    @Transactional(readOnly = true)
    public Map<String, Object> verificarDisponibilidad(Long doctorId, String fecha, String hora) {
        LocalDateTime fechaHora = LocalDateTime.of(
                LocalDate.parse(fecha), LocalTime.parse(hora));

        boolean ocupado = citaRepo.existsByDoctor_IdDoctorAndEstadoNotAndFechaHora(
                doctorId, Cita.CitaEstado.CANCELADA, fechaHora);

        return Map.of(
                "doctorId",  doctorId,
                "fechaHora", fechaHora.toString(),
                "disponible", !ocupado,
                "mensaje", ocupado
                        ? "Ya existe una cita activa a esa hora."
                        : "Horario disponible."
        );
    }

    // Helpers internos
    private Cita buscarPorId(Long id) {
        return citaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cita no encontrada: " + id));
    }
}
