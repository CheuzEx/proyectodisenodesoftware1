package clinica.dto.historial;

import clinica.dto.cita.CitaResponseDTO;
import clinica.dto.paciente.PacienteResumenDTO;
import clinica.dto.receta.RecetaResponseDTO;
import clinica.model.HistorialMedico;
import clinica.model.Receta;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialResponseDTO {

    private Long idHistorial;
    private LocalDate fechaConsulta;
    private String diagnostico;
    private String tratamiento;
    private PacienteResumenDTO paciente;
    private CitaResponseDTO cita;
    private List<RecetaResponseDTO> recetas;

    public static HistorialResponseDTO fromEntity(HistorialMedico h) {
        if (h == null) return null;

        HistorialResponseDTO dto = new HistorialResponseDTO();

        dto.setIdHistorial(h.getIdHistorial());
        dto.setFechaConsulta(h.getFechaConsulta());
        dto.setDiagnostico(h.getDiagnostico());
        dto.setTratamiento(h.getTratamiento());
        dto.setPaciente(PacienteResumenDTO.fromEntity(h.getPaciente()));
        dto.setCita(CitaResponseDTO.fromEntity(h.getCita()));

        Long idCita = h.getCita() != null ? h.getCita().getIdCita() : null;

        List<RecetaResponseDTO> recetasFiltradas =
                h.getRecetas() == null ? List.of() :
                        h.getRecetas()
                                .stream()
                                .filter(r -> perteneceALaMismaCita(r, idCita))
                                .map(RecetaResponseDTO::fromEntity)
                                .collect(Collectors.toList());

        dto.setRecetas(recetasFiltradas);

        return dto;
    }

    private static boolean perteneceALaMismaCita(Receta r, Long idCita) {
        if (r == null || idCita == null || r.getCita() == null) {
            return false;
        }

        return idCita.equals(r.getCita().getIdCita());
    }

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