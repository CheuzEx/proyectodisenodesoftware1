package clinica.dto.doctor;

import clinica.model.Doctor;

public class DoctorResumenDTO {

    private Long idDoctor;
    private String nombre;
    private String apellido;
    private String telefono;

    // Mapeo desde entidad
    public static DoctorResumenDTO fromEntity(Doctor d) {
        if (d == null) return null;
        DoctorResumenDTO dto = new DoctorResumenDTO();
        dto.setIdDoctor(d.getIdDoctor());
        dto.setNombre(d.getNombre());
        dto.setApellido(d.getApellido());
        dto.setTelefono(d.getTelefono());
        return dto;
    }

    // Getters y Setters
    public Long getIdDoctor() { return idDoctor; }
    public void setIdDoctor(Long idDoctor) { this.idDoctor = idDoctor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
