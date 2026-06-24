package clinica.repository;

import clinica.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Receta.
 * Proporciona métodos para consultar recetas por cita o por historial médico.
 */
@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    // Busca todas las recetas asociadas a una cita específica (por ID de cita)
    List<Receta> findByCita_IdCita(Long idCita);

    // Busca todas las recetas asociadas a un historial médico específico (por ID de historial)
    List<Receta> findByHistorial_IdHistorial(Long idHistorial);
}
