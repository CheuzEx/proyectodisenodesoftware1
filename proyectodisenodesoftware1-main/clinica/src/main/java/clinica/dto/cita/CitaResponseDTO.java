package clinica.dto.cita;

import clinica.dto.doctor.DoctorResumenDTO;
import clinica.dto.especialidad.EspecialidadResponseDTO;
import clinica.dto.paciente.PacienteResumenDTO;
import clinica.model.Cita;

import java.time.LocalDateTime;

public class CitaResponseDTO {

    private Long idCita;
    private LocalDateTime fechaHora;
    private String motivo;
    private String estado;
    private PacienteResumenDTO paciente;
    private DoctorResumenDTO doctor;
    private EspecialidadResponseDTO especialidad;

    // Mapeo desde entidad
    public static CitaResponseDTO fromEntity(Cita c) {
        if (c == null) return null;
        CitaResponseDTO dto = new CitaResponseDTO();
        dto.setIdCita(c.getIdCita());
        dto.setFechaHora(c.getFechaHora());
        dto.setMotivo(c.getMotivo());

        dto.setEstado(c.getEstado() != null ? c.getEstado().name() : null);

        dto.setPaciente(PacienteResumenDTO.fromEntity(c.getPaciente()));
        dto.setDoctor(DoctorResumenDTO.fromEntity(c.getDoctor()));
        dto.setEspecialidad(EspecialidadResponseDTO.fromEntity(c.getEspecialidad()));
        return dto;
    }

    // Getters y Setters
    public Long getIdCita() { return idCita; }
    public void setIdCita(Long idCita) { this.idCita = idCita; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public PacienteResumenDTO getPaciente() { return paciente; }
    public void setPaciente(PacienteResumenDTO paciente) { this.paciente = paciente; }

    public DoctorResumenDTO getDoctor() { return doctor; }
    public void setDoctor(DoctorResumenDTO doctor) { this.doctor = doctor; }

    public EspecialidadResponseDTO getEspecialidad() { return especialidad; }
    public void setEspecialidad(EspecialidadResponseDTO especialidad) { this.especialidad = especialidad; }
}
