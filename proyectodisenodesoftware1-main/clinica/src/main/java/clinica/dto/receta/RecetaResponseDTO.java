package clinica.dto.receta;

import clinica.model.Receta;

public class RecetaResponseDTO {

    private Long idReceta;
    private String medicamento;
    private String dosis;
    private String frecuencia;
    private Integer duracion;

    // Mapeo desde entidad
    public static RecetaResponseDTO fromEntity(Receta r) {
        if (r == null) return null;
        RecetaResponseDTO dto = new RecetaResponseDTO();
        dto.setIdReceta(r.getIdReceta());
        dto.setMedicamento(r.getMedicamento());
        dto.setDosis(r.getDosis());
        dto.setFrecuencia(r.getFrecuencia());
        dto.setDuracion(r.getDuracion());
        return dto;
    }

    // Getters y Setters
    public Long getIdReceta() { return idReceta; }
    public void setIdReceta(Long idReceta) { this.idReceta = idReceta; }

    public String getMedicamento() { return medicamento; }
    public void setMedicamento(String medicamento) { this.medicamento = medicamento; }

    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }

    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }

    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }
}
