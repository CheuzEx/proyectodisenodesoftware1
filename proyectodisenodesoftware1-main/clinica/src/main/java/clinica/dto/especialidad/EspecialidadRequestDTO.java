package clinica.dto.especialidad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EspecialidadRequestDTO {

    @NotBlank(message = "El nombre de la especialidad es obligatorio")
    @Size(max = 100)
    private String nombre;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
