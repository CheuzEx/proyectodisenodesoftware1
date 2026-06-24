package clinica.repository;

import clinica.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    // Busca el usuario vinculado a un doctor
    Optional<Usuario> findByDoctor_IdDoctor(Long idDoctor);
}
