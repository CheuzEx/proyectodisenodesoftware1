package clinica.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarToken(UserDetails userDetails) {
        String rol = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("");

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraerUsername(String token) {
        return parsear(token).getBody().getSubject();
    }

    public String extraerRol(String token) {
        return (String) parsear(token).getBody().get("rol");
    }

    public boolean esValido(String token, UserDetails userDetails) {
        try {
            String username = extraerUsername(token);
            return username.equals(userDetails.getUsername()) && !estaExpirado(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean estaExpirado(String token) {
        return parsear(token).getBody().getExpiration().before(new Date());
    }

    private Jws<Claims> parsear(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
    }
}
