package clinica.repository;

import clinica.model.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad HistorialMedico.
 * Proporciona métodos para consultar y gestionar historiales médicos.
 */
@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {

    // Verifica si existe un historial asociado a una cita específica (por ID de cita)
    boolean existsByCita_IdCita(Long idCita);

    // Busca un historial médico por el ID de su cita asociada (devuelve Optional)
    Optional<HistorialMedico> findByCita_IdCita(Long idCita);

    // Busca un historial médico por el ID del paciente (devuelve Optional)
    Optional<HistorialMedico> findByPaciente_IdPaciente(Long idPaciente);

    // Elimina todos los historiales médicos de un paciente (por ID de paciente)
    void deleteByPaciente_IdPaciente(Long idPaciente);

    // Consulta personalizada: obtiene los historiales reales (con fecha de consulta no nula)
    // de un paciente, ordenados por fecha descendente (más reciente primero)
    @Query("SELECT h FROM HistorialMedico h WHERE h.paciente.idPaciente = :idPaciente " +
            "AND h.fechaConsulta IS NOT NULL " +
            "ORDER BY h.fechaConsulta DESC")
    List<HistorialMedico> findHistorialesRealesPorPaciente(@Param("idPaciente") Long idPaciente);
}
