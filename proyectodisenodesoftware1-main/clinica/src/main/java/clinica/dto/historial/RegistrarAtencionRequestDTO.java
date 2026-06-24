package clinica.dto.historial;

import clinica.dto.receta.RecetaRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class RegistrarAtencionRequestDTO {

    @NotNull(message = "El citaId es obligatorio")
    private Long citaId;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    @NotBlank(message = "El tratamiento es obligatorio")
    private String tratamiento;

    private LocalDate fechaConsulta;

    @Valid
    private List<RecetaRequestDTO> recetas;

    // Getters y Setters
    public Long getCitaId() { return citaId; }
    public void setCitaId(Long citaId) { this.citaId = citaId; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }

    public LocalDate getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(LocalDate fechaConsulta) { this.fechaConsulta = fechaConsulta; }

    public List<RecetaRequestDTO> getRecetas() { return recetas; }
    public void setRecetas(List<RecetaRequestDTO> recetas) { this.recetas = recetas; }
}
