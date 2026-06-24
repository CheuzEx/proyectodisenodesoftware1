package clinica.dto.paciente;

import clinica.model.Paciente;
import java.time.LocalDate;

public class PacienteDetalleDTO {

    private Long idPaciente;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String direccion;
    private String telefono;
    private String correo;

    // Mapeo desde entidad
    public static PacienteDetalleDTO fromEntity(Paciente p) {
        if (p == null) return null;
        PacienteDetalleDTO dto = new PacienteDetalleDTO();
        dto.setIdPaciente(p.getIdPaciente());
        dto.setNombre(p.getNombre());
        dto.setApellido(p.getApellido());
        dto.setFechaNacimiento(p.getFechaNacimiento());
        dto.setSexo(p.getSexo());
        dto.setDireccion(p.getDireccion());
        dto.setTelefono(p.getTelefono());
        dto.setCorreo(p.getCorreo());
        return dto;
    }

    // Getters y Setters
    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}
