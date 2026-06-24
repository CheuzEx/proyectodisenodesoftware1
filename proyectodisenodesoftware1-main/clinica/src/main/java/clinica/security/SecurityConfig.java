package clinica.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Públicas
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/*.html", "/", "/login", "/login.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/doctor/**", "/pacientes/**",
                                "/historiales/**", "/citas/**").permitAll()

                        // Recetas (ANTES de las reglas genéricas)
                        .requestMatchers(HttpMethod.GET, "/api/recetas/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        .requestMatchers(HttpMethod.POST, "/api/recetas/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        .requestMatchers(HttpMethod.POST, "/api/pacientes/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        .requestMatchers(HttpMethod.PUT, "/api/pacientes/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        // Citas
                        .requestMatchers(HttpMethod.GET, "/api/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        .requestMatchers(HttpMethod.POST, "/api/citas/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        .requestMatchers(HttpMethod.PUT, "/api/citas/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        .requestMatchers(HttpMethod.DELETE, "/api/citas/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        // Doctor
                        .requestMatchers(HttpMethod.PUT, "/api/doctores/me")
                        .hasRole("DOCTOR")

                        // Historiales
                        .requestMatchers(HttpMethod.POST, "/api/historiales/**")
                        .hasAnyRole("ADMIN", "DOCTOR")

                        // Reglas genéricas para el resto de APIs
                        .requestMatchers(HttpMethod.POST, "/api/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CommandLineRunner generarHash(PasswordEncoder encoder) {
        return args -> {
            System.out.println(encoder.encode("123456"));
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}