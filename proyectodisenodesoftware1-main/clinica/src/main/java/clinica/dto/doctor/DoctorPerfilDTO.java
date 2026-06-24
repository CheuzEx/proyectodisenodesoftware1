package clinica.dto.doctor;

import clinica.dto.especialidad.EspecialidadResponseDTO;
import clinica.model.Doctor;

import java.util.List;
import java.util.stream.Collectors;

public class DoctorPerfilDTO {

    private Long idDoctor;
    private String nombre;
    private String apellido;
    private String telefono;
    private String direccion;
    private List<EspecialidadResponseDTO> especialidades;

    // Mapeo desde entidad
    public static DoctorPerfilDTO fromEntity(Doctor d) {
        if (d == null) return null;
        DoctorPerfilDTO dto = new DoctorPerfilDTO();
        dto.setIdDoctor(d.getIdDoctor());
        dto.setNombre(d.getNombre());
        dto.setApellido(d.getApellido());
        dto.setTelefono(d.getTelefono());
        dto.setDireccion(d.getDireccion());
        dto.setEspecialidades(
            d.getEspecialidades() == null ? List.of() :
            d.getEspecialidades().stream()
                .map(EspecialidadResponseDTO::fromEntity)
                .collect(Collectors.toList())
        );
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

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public List<EspecialidadResponseDTO> getEspecialidades() { return especialidades; }
    public void setEspecialidades(List<EspecialidadResponseDTO> especialidades) { this.especialidades = especialidades; }
}
