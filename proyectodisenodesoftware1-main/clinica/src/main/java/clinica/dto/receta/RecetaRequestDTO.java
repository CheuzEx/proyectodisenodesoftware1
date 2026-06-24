package clinica.dto.receta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para recibir los datos necesarios al crear una nueva receta médica.
 * Se utiliza en el endpoint POST /api/recetas para mapear el JSON enviado por el cliente.
 * Contiene validaciones para asegurar que los campos obligatorios no estén vacíos.
 */
public class RecetaRequestDTO {

    @NotNull(message = "La cita es obligatoria")  // El ID de la cita no puede ser nulo
    private Long citaId;

    @NotBlank(message = "El medicamento es obligatorio") // No puede ser nulo, vacío o solo espacios
    private String medicamento;

    @NotBlank(message = "La dosis es obligatoria") // No puede ser nulo, vacío o solo espacios
    private String dosis;

    private String frecuencia;   // Opcional, ej. "cada 8 horas"

    private Integer duracion;    // Opcional, duración en días

    // Getters y Setters
    public Long getCitaId() {
        return citaId;
    }

    public void setCitaId(Long citaId) {
        this.citaId = citaId;
    }

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }
}
