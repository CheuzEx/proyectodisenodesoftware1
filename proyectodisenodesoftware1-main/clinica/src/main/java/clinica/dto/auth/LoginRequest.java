package clinica.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO (Data Transfer Object) para recibir las credenciales de inicio de sesión.
 * Se utiliza en el endpoint /api/auth/login para mapear el JSON enviado por el cliente.
 */
public class LoginRequest {

    @NotBlank(message = "El username es obligatorio")  // Valida que no sea nulo, vacío o solo espacios
    private String username;

    @NotBlank(message = "La contraseña es obligatoria") // Valida que no sea nula, vacía o solo espacios
    private String password;

    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
