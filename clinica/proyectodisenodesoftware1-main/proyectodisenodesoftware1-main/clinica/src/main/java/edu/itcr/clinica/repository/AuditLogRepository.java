package edu.itcr.clinica.repository;

import edu.itcr.clinica.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /** Devuelve todos los registros de auditoría de una tabla específica. */
    List<AuditLog> findByTablaOrderByFechaHoraDesc(String tabla);

    /** Devuelve el historial de cambios de un registro específico. */
    List<AuditLog> findByTablaAndIdRegistroOrderByFechaHoraDesc(String tabla, Long idRegistro);
}
