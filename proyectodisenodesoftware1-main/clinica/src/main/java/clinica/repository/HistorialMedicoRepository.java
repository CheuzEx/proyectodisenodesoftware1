package clinica.repository;

import clinica.model.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {

    boolean existsByCita_IdCita(Long idCita);

    Optional<HistorialMedico> findByCita_IdCita(Long idCita);

    Optional<HistorialMedico> findByPaciente_IdPaciente(Long idPaciente);
    void deleteByPaciente_IdPaciente(Long idPaciente);
    @Query("SELECT h FROM HistorialMedico h WHERE h.paciente.idPaciente = :idPaciente " +
            "AND h.fechaConsulta IS NOT NULL " +
            "ORDER BY h.fechaConsulta DESC")
    List<HistorialMedico> findHistorialesRealesPorPaciente(@Param("idPaciente") Long idPaciente);
}