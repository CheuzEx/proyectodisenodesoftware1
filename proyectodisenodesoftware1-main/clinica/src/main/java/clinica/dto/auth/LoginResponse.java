package clinica.dto.auth;

/**
 * DTO (Data Transfer Object) para la respuesta del inicio de sesión.
 * Contiene el token JWT generado, el nombre de usuario y el rol del usuario autenticado.
 * Se envía al cliente después de una autenticación exitosa.
 */
public class LoginResponse {

    private String token;    // Token JWT para autenticar peticiones posteriores
    private String username; // Nombre de usuario autenticado
    private String rol;      // Rol/permiso del usuario (ej. "ROLE_ADMIN", "ROLE_DOCTOR")

    public LoginResponse(String token, String username, String rol) {
        this.token    = token;
        this.username = username;
        this.rol      = rol;
    }

    // Getters
    public String getToken()    { return token; }
    public String getUsername() { return username; }
    public String getRol()      { return rol; }
}
