package clinica.repository;

import clinica.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByCita_IdCita(Long idCita);
    List<Receta> findByHistorial_IdHistorial(Long idHistorial);
}