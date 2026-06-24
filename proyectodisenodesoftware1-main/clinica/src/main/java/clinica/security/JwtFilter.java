package clinica.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta cada petición HTTP para validar el token JWT
 * y autenticar al usuario en el contexto de Spring Security.
 * Se ejecuta una sola vez por petición (OncePerRequestFilter).
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;                      // Utilidad para manejar JWT
    private final UsuarioDetailsService userDetailsService; // Carga datos del usuario

    public JwtFilter(JwtUtil jwtUtil, UsuarioDetailsService userDetailsService) {
        this.jwtUtil            = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 1. Obtener el encabezado Authorization de la petición
        String authHeader = request.getHeader("Authorization");

        // 2. Verificar que el encabezado exista y comience con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extraer el token (eliminar "Bearer " del inicio)
            String token = authHeader.substring(7);
            try {
                // 3. Extraer el username del token
                String username = jwtUtil.extraerUsername(token);

                // 4. Si el username es válido y no hay autenticación en el contexto actual
                if (username != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 5. Cargar los detalles del usuario desde la base de datos
                    UserDetails userDetails =
                            userDetailsService.loadUserByUsername(username);

                    // 6. Validar el token (firma, expiración, que coincida con el usuario)
                    if (jwtUtil.esValido(token, userDetails)) {
                        // 7. Crear un objeto de autenticación con el usuario y sus roles
                        var auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        // 8. Agregar detalles de la petición (IP, sesión, etc.)
                        auth.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));

                        // 9. Establecer la autenticación en el contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception ignored) {
                // Si ocurre cualquier error (token inválido, expirado, etc.), se ignora
                // y el filtro continúa sin autenticar al usuario
            }
        }

        // 10. Continuar con la cadena de filtros (permite que la petición siga su curso)
        chain.doFilter(request, response);
    }
}
