package clinica.security;

import clinica.model.Usuario;
import clinica.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("LOGIN INTENTADO: " + username);

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("USUARIO NO ENCONTRADO");
                    return new UsernameNotFoundException(
                            "Usuario no encontrado: " + username);
                });

        System.out.println("USUARIO ENCONTRADO: " + usuario.getUsername());
        System.out.println("HASH BD: " + usuario.getPassword());

        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority(usuario.getRol()))
        );
    }
}
