package clinica.dto.doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DoctorRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String apellido;

    @Size(max = 50)
    private String telefono;

    @Size(max = 150)
    private String direccion;

    // Solo requeridos al CREAR un doctor nuevo (pueden ser null en edición)
    @Size(max = 100, message = "El username no puede superar 100 caracteres")
    private String username;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}