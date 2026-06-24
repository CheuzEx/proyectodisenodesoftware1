package clinica.dto.cita;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CitaRequestDTO {

    @NotBlank(message = "La fecha es obligatoria")
    private String fecha;

    @NotBlank(message = "La hora es obligatoria")
    private String hora;

    private String motivo;

    @NotNull(message = "El pacienteId es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El doctorId es obligatorio")
    private Long doctorId;

    @NotNull(message = "El especialidadId es obligatorio")
    private Long especialidadId;

    // Getters y Setters
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Long getEspecialidadId() { return especialidadId; }
    public void setEspecialidadId(Long especialidadId) { this.especialidadId = especialidadId; }
}
