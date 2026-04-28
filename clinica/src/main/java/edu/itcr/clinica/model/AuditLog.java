package edu.itcr.clinica.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Registro de auditoría automática de cambios en las tablas del sistema.
 * Cada fila representa una operación INSERT, UPDATE o DELETE sobre cualquier entidad.
 */
@Entity
@Table(name = "audit_log", schema = "clinica")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_audit")
    private Long idAudit;

    /** Nombre de la tabla afectada (ej: "paciente", "cita"). */
    @Column(name = "tabla", nullable = false, length = 50)
    private String tabla;

    /** ID del registro afectado. */
    @Column(name = "id_registro")
    private Long idRegistro;

    /** Tipo de operación: INSERT, UPDATE o DELETE. */
    @Column(name = "operacion", nullable = false, length = 10)
    private String operacion;

    /** Descripción o detalle del cambio realizado. */
    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;

    /** Fecha y hora en que ocurrió el cambio (asignada automáticamente). */
    @CreationTimestamp
    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    // Constructors
    public AuditLog() {}

    public AuditLog(String tabla, Long idRegistro, String operacion, String detalle) {
        this.tabla       = tabla;
        this.idRegistro  = idRegistro;
        this.operacion   = operacion;
        this.detalle     = detalle;
    }

    // Getters y Setters
    public Long getIdAudit()                    { return idAudit; }
    public void setIdAudit(Long idAudit)        { this.idAudit = idAudit; }

    public String getTabla()                    { return tabla; }
    public void setTabla(String tabla)          { this.tabla = tabla; }

    public Long getIdRegistro()                 { return idRegistro; }
    public void setIdRegistro(Long idRegistro)  { this.idRegistro = idRegistro; }

    public String getOperacion()                { return operacion; }
    public void setOperacion(String operacion)  { this.operacion = operacion; }

    public String getDetalle()                  { return detalle; }
    public void setDetalle(String detalle)      { this.detalle = detalle; }

    public LocalDateTime getFechaHora()         { return fechaHora; }
    public void setFechaHora(LocalDateTime f)   { this.fechaHora = f; }
}
