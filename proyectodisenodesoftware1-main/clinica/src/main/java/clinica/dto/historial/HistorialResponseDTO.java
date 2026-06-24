package clinica.dto.historial;

import clinica.dto.cita.CitaResponseDTO;
import clinica.dto.paciente.PacienteResumenDTO;
import clinica.dto.receta.RecetaResponseDTO;
import clinica.model.HistorialMedico;
import clinica.model.Receta;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para enviar la información completa de un historial médico,
 * incluyendo el paciente, la cita asociada y las recetas filtradas
 * que corresponden a esa cita.
 */
public class HistorialResponseDTO {

    private Long idHistorial;
    private LocalDate fechaConsulta;
    private String diagnostico;
    private String tratamiento;
    private PacienteResumenDTO paciente;    // Datos resumidos del paciente
    private CitaResponseDTO cita;           // Datos de la cita asociada (puede ser null)
    private List<RecetaResponseDTO> recetas; // Recetas que pertenecen a la misma cita

    /**
     * Convierte una entidad HistorialMedico a su DTO de respuesta.
     * Filtra las recetas para incluir solo aquellas que están asociadas
     * a la misma cita que el historial.
     *
     * @param h entidad HistorialMedico (puede ser null)
     * @return HistorialResponseDTO o null si la entidad es null
     */
    public static HistorialResponseDTO fromEntity(HistorialMedico h) {
        if (h == null) return null;

        HistorialResponseDTO dto = new HistorialResponseDTO();

        // Mapeo de campos básicos
        dto.setIdHistorial(h.getIdHistorial());
        dto.setFechaConsulta(h.getFechaConsulta());
        dto.setDiagnostico(h.getDiagnostico());
        dto.setTratamiento(h.getTratamiento());

        // Conversión de objetos relacionados
        dto.setPaciente(PacienteResumenDTO.fromEntity(h.getPaciente()));
        dto.setCita(CitaResponseDTO.fromEntity(h.getCita()));

        // Obtener el ID de la cita (puede ser null)
        Long idCita = h.getCita() != null ? h.getCita().getIdCita() : null;

        // Filtrar las recetas: solo las que pertenecen a la misma cita
        List<RecetaResponseDTO> recetasFiltradas =
                h.getRecetas() == null ? List.of() :   // Si no hay recetas, lista vacía
                        h.getRecetas()
                                .stream()
                                .filter(r -> perteneceALaMismaCita(r, idCita))
                                .map(RecetaResponseDTO::fromEntity)
                                .collect(Collectors.toList());

        dto.setRecetas(recetasFiltradas);

        return dto;
    }

    /**
     * Determina si una receta pertenece a la misma cita que el historial.
     * Solo devuelve true si:
     *   - la receta no es null
     *   - el idCita no es null
     *   - la receta tiene una cita asociada
     *   - los IDs de cita coinciden
     */
    private static boolean perteneceALaMismaCita(Receta r, Long idCita) {
        if (r == null || idCita == null || r.getCita() == null) {
            return false;
        }
        return idCita.equals(r.getCita().getIdCita());
    }

    // Getters y Setters
    public Long getIdHistorial() { return idHistorial; }
    public void setIdHistorial(Long idHistorial) { this.idHistorial = idHistorial; }

    public LocalDate getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(LocalDate fechaConsulta) { this.fechaConsulta = fechaConsulta; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }

    public PacienteResumenDTO getPaciente() { return paciente; }
    public void setPaciente(PacienteResumenDTO paciente) { this.paciente = paciente; }

    public CitaResponseDTO getCita() { return cita; }
    public void setCita(CitaResponseDTO cita) { this.cita = cita; }

    public List<RecetaResponseDTO> getRecetas() { return recetas; }
    public void setRecetas(List<RecetaResponseDTO> recetas) { this.recetas = recetas; }
}
