package edu.itcr.clinica.repository;

import edu.itcr.clinica.model.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {

    // Verifica si un historial ya existe para una cita específica
    boolean existsByCita_IdCita(Long idCita);

    // Obtener historial asociado a una cita
    Optional<HistorialMedico> findByCita_IdCita(Long idCita);

    
    // Obtener historiales reales de un paciente (excluye el historial inicial vacío) ordenados del más reciente al más antiguo
    @Query("SELECT h FROM HistorialMedico h WHERE h.paciente.idPaciente = :idPaciente " +
           "AND h.fechaConsulta IS NOT NULL " +
           "ORDER BY h.fechaConsulta DESC")
    List<HistorialMedico> findHistorialesRealesPorPaciente(@Param("idPaciente") Long idPaciente);
}
