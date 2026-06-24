package clinica.repository;

import clinica.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Proporciona métodos para gestionar usuarios del sistema,
 * principalmente para autenticación y control de acceso.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca un usuario por su nombre de usuario (username) para autenticación
    Optional<Usuario> findByUsername(String username);
}
