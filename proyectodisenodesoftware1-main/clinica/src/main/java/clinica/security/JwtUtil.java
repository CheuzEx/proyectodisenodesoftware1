package clinica.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utilidad para la generación, validación y extracción de información
 * de tokens JWT (JSON Web Tokens) utilizados en la autenticación.
 */
@Component
public class JwtUtil {

    // Clave secreta para firmar los tokens (definida en application.properties)
    @Value("${jwt.secret}")
    private String secret;

    // Tiempo de expiración en milisegundos (valor por defecto: 1 hora)
    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    // Obtiene la clave de firma a partir del secreto (usando HMAC SHA-256)
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Genera un token JWT para un usuario autenticado.
     * Incluye el username como subject, el rol como claim adicional,
     * la fecha de emisión y la fecha de expiración.
     *
     * @param userDetails detalles del usuario autenticado
     * @return token JWT firmado
     */
    public String generarToken(UserDetails userDetails) {
        // Extrae el primer rol del usuario (asume que tiene al menos uno)
        String rol = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("");

        return Jwts.builder()
                .setSubject(userDetails.getUsername())          // Nombre de usuario
                .claim("rol", rol)                              // Rol del usuario
                .setIssuedAt(new Date())                        // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // Expiración
                .signWith(getKey(), SignatureAlgorithm.HS256)   // Firma con clave secreta
                .compact();
    }

    /**
     * Extrae el nombre de usuario (subject) del token.
     *
     * @param token JWT
     * @return username contenido en el token
     */
    public String extraerUsername(String token) {
        return parsear(token).getBody().getSubject();
    }

    /**
     * Extrae el rol almacenado en el token.
     *
     * @param token JWT
     * @return rol del usuario
     */
    public String extraerRol(String token) {
        return (String) parsear(token).getBody().get("rol");
    }

    /**
     * Valida que el token sea correcto y no haya expirado.
     * Compara el username del token con el del UserDetails proporcionado.
     *
     * @param token        JWT a validar
     * @param userDetails  detalles del usuario actual
     * @return true si el token es válido, false en caso contrario
     */
    public boolean esValido(String token, UserDetails userDetails) {
        try {
            String username = extraerUsername(token);
            return username.equals(userDetails.getUsername()) && !estaExpirado(token);
        } catch (JwtException e) {
            return false; // Token inválido (firma, estructura, etc.)
        }
    }

    /**
     * Verifica si el token ha expirado comparando su fecha de expiración con la fecha actual.
     *
     * @param token JWT
     * @return true si ha expirado, false si aún es válido
     */
    private boolean estaExpirado(String token) {
        return parsear(token).getBody().getExpiration().before(new Date());
    }

    /**
     * Parsea el token JWT y valida su firma.
     * Si la firma es inválida o el token está malformado, lanza una excepción JwtException.
     *
     * @param token JWT
     * @return objeto Jws con los claims y la información de la firma
     */
    private Jws<Claims> parsear(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())          // Establece la clave para verificar firma
                .build()
                .parseClaimsJws(token);           // Lanza excepción si algo falla
    }
}
