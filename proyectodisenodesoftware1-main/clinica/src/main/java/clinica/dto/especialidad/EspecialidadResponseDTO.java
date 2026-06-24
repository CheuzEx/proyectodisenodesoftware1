package clinica.dto.especialidad;

import clinica.model.Especialidad;

public class EspecialidadResponseDTO {

    private Long idEspecialidad;
    private String nomEspecialidad;

    // Mapeo desde entidad
    public static EspecialidadResponseDTO fromEntity(Especialidad e) {
        if (e == null) return null;
        EspecialidadResponseDTO dto = new EspecialidadResponseDTO();
        dto.setIdEspecialidad(e.getIdEspecialidad());
        dto.setNomEspecialidad(e.getNomEspecialidad());
        return dto;
    }

    // Getters y Setters
    public Long getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(Long idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public String getNomEspecialidad() { return nomEspecialidad; }
    public void setNomEspecialidad(String nomEspecialidad) { this.nomEspecialidad = nomEspecialidad; }
}
