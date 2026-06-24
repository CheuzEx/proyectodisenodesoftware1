package clinica.controller;

import clinica.dto.auth.LoginRequest;
import clinica.dto.auth.LoginResponse;
import clinica.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la autenticación de usuarios.
 * Expone el endpoint /api/auth/login para que los clientes
 * envíen credenciales y reciban un token JWT.
 * 
 * Utiliza Spring Security para la autenticación y JWT para
 * la generación del token de acceso.
 */
@RestController
@RequestMapping("/api/auth")          // Ruta base para todos los endpoints de este controlador
@CrossOrigin                          // Permite peticiones desde dominios externos (CORS)
public class AuthController {

    // Dependencias inyectadas por constructor
    private final AuthenticationManager authManager;  // Gestiona la autenticación (delegada a Spring Security)
    private final JwtUtil jwtUtil;                    // Utilidad para crear y validar tokens JWT

    /**
     * Constructor con inyección de dependencias.
     * Spring inyectará automáticamente los beans necesarios.
     *
     * @param authManager el gestor de autenticación de Spring Security
     * @param jwtUtil     la utilidad para manejar JWT
     */
    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil     = jwtUtil;
    }

    /**
     * Endpoint para iniciar sesión.
     * Recibe un objeto LoginRequest con username y password,
     * los valida y, si son correctos, genera un token JWT.
     *
     * @param request objeto con las credenciales (validado con @Valid)
     * @return LoginResponse con el token, nombre de usuario y rol
     */
    @PostMapping("/login")   // Maneja peticiones POST a /api/auth/login
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        // 1. Crear un token de autenticación con las credenciales proporcionadas
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                );

        // 2. Delegar la autenticación al AuthenticationManager de Spring Security.
        //    Esto dispara la carga del UserDetailsService y la verificación de contraseña.
        Authentication auth = authManager.authenticate(authToken);

        // 3. Obtener el objeto UserDetails del principal autenticado.
        //    Contiene la información del usuario (nombre, autoridades, etc.).
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        // 4. Generar el token JWT usando la utilidad JwtUtil.
        String token = jwtUtil.generarToken(userDetails);

        // 5. Extraer el primer rol (autoridad) del usuario para incluirlo en la respuesta.
        //    Se asume que el usuario tiene al menos un rol (p.ej. "ROLE_ADMIN").
        String rol = userDetails.getAuthorities().stream()
                .findFirst()                      // Toma el primer elemento (si existe)
                .map(a -> a.getAuthority())       // Obtiene el nombre del rol (String)
                .orElse("");                     // Si no hay roles, devuelve cadena vacía

        // 6. Devolver la respuesta con el token, el username y el rol.
        return new LoginResponse(token, userDetails.getUsername(), rol);
    }
}
