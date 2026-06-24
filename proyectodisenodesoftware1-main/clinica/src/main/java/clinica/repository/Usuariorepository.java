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

    /**
     * Busca un usuario por su nombre de usuario (username)
     * para autenticación y control de acceso.
     *
     * @param username nombre de usuario.
     * @return usuario encontrado si existe.
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Busca el usuario asociado a un doctor específico.
     *
     * Este método navega la relación Usuario -> Doctor
     * utilizando el identificador del médico.
     *
     * @param idDoctor identificador único del doctor.
     * @return usuario asociado al doctor si existe.
     */
    Optional<Usuario> findByDoctor_IdDoctor(Long idDoctor);
}
