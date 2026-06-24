package clinica.security;

import clinica.model.Usuario;
import clinica.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio que implementa UserDetailsService de Spring Security.
 * Se encarga de cargar los datos de un usuario desde la base de datos
 * usando su nombre de usuario (username) para la autenticación.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga los detalles del usuario por su nombre de usuario.
     * Este método es llamado por Spring Security durante el proceso de autenticación.
     *
     * @param username nombre de usuario a buscar
     * @return UserDetails con la información del usuario (username, password, roles)
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Log para depuración: muestra qué usuario está intentando autenticarse
        System.out.println("LOGIN INTENTADO: " + username);

        // Buscar el usuario en la base de datos por su username
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> {
                    // Log de error si no se encuentra el usuario
                    System.out.println("USUARIO NO ENCONTRADO");
                    return new UsernameNotFoundException(
                            "Usuario no encontrado: " + username);
                });

        // Logs de depuración: muestra los datos del usuario encontrado
        System.out.println("USUARIO ENCONTRADO: " + usuario.getUsername());
        System.out.println("HASH BD: " + usuario.getPassword());

        // Crear y devolver un objeto User de Spring Security con:
        // - username
        // - password (hashed)
        // - lista de autoridades (roles) convertidas a SimpleGrantedAuthority
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority(usuario.getRol()))
        );
    }
}
