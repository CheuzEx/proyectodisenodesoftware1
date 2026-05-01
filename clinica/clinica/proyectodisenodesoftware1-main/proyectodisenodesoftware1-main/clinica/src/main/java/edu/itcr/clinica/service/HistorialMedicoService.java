package edu.itcr.clinica.service;

import edu.itcr.clinica.model.Cita;
import edu.itcr.clinica.model.HistorialMedico;
import edu.itcr.clinica.model.Paciente;
import edu.itcr.clinica.model.Receta;
import edu.itcr.clinica.repository.CitaRepository;
import edu.itcr.clinica.repository.HistorialMedicoRepository;
import edu.itcr.clinica.repository.PacienteRepository;
import edu.itcr.clinica.repository.RecetaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class HistorialMedicoService {

    private final HistorialMedicoRepository historialRepo;
    private final CitaRepository citaRepo;
    private final RecetaRepository recetaRepo;
    private final PacienteRepository pacienteRepo;

    public HistorialMedicoService(HistorialMedicoRepository historialRepo,
                                  CitaRepository citaRepo,
                                  RecetaRepository recetaRepo,
                                  PacienteRepository pacienteRepo) {
        this.historialRepo = historialRepo;
        this.citaRepo      = citaRepo;
        this.recetaRepo    = recetaRepo;
        this.pacienteRepo  = pacienteRepo;
    }

    // DTO interno reutilizable para recetas dentro del registro de atención
    public static class RecetaInput {
        public String  medicamento;
        public String  dosis;
        public String  frecuencia;
        public Integer duracion;
    }

    /** Devuelve todos los historiales médicos del sistema. */
    @Transactional(readOnly = true)
    public List<HistorialMedico> listar() {
        return historialRepo.findAll();
    }

    /** Devuelve los historiales de un paciente, del más reciente al más antiguo. */
    @Transactional(readOnly = true)
    public List<HistorialMedico> listarPorPaciente(Long idPaciente) {
        return historialRepo.findHistorialesRealesPorPaciente(idPaciente);
    }

    /** Busca el historial asociado a una cita específica. */
    @Transactional(readOnly = true)
    public HistorialMedico buscarPorCita(Long idCita) {
        return historialRepo.findByCita_IdCita(idCita)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Historial no encontrado para la cita: " + idCita));
    }

    /** Devuelve un paciente por ID, lanzando 404 si no existe. */
    @Transactional(readOnly = true)
    public Paciente buscarPaciente(Long idPaciente) {
        return pacienteRepo.findById(idPaciente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Paciente no encontrado: " + idPaciente));
    }

    /**
     * Registra la atención de una cita:
     * 1. Valida que la cita exista y esté en estado PROGRAMADA.
     * 2. Valida que aún no tenga historial asociado.
     * 3. Crea el historial con diagnóstico, tratamiento y recetas.
     * 4. Marca la cita como ATENDIDA.
     */
    @Transactional
    public HistorialMedico registrarAtencion(Long citaId,
                                             String diagnostico,
                                             String tratamiento,
                                             LocalDate fechaConsulta,
                                             List<RecetaInput> recetasInput) {

        if (citaId == null || esVacio(diagnostico) || esVacio(tratamiento)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "citaId, diagnostico y tratamiento son obligatorios.");
        }

        Cita cita = citaRepo.findById(citaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cita no encontrada: " + citaId));

        if (cita.getEstado() != Cita.CitaEstado.PROGRAMADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La cita no está en estado PROGRAMADA.");
        }
        if (historialRepo.existsByCita_IdCita(cita.getIdCita())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La cita ya tiene un historial registrado.");
        }

        // Construir historial
        HistorialMedico historial = new HistorialMedico();
        historial.setCita(cita);
        historial.setPaciente(cita.getPaciente());
        historial.setDiagnostico(diagnostico.trim());
        historial.setTratamiento(tratamiento.trim());
        historial.setFechaConsulta(resolverFecha(fechaConsulta, cita));

        historial = historialRepo.save(historial);

        // Persistir recetas válidas
        List<Receta> recetasCreadas = new ArrayList<>();
        if (recetasInput != null) {
            for (RecetaInput r : recetasInput) {
                if (r == null || esVacio(r.medicamento) || esVacio(r.dosis)) continue;
                Receta receta = new Receta();
                receta.setHistorial(historial);
                receta.setMedicamento(r.medicamento.trim());
                receta.setDosis(r.dosis.trim());
                receta.setFrecuencia(esVacio(r.frecuencia) ? null : r.frecuencia.trim());
                receta.setDuracion(r.duracion);
                recetasCreadas.add(recetaRepo.save(receta));
            }
        }

        // Marcar cita como atendida
        cita.setEstado(Cita.CitaEstado.ATENDIDA);
        citaRepo.save(cita);

        historial.setRecetas(recetasCreadas);
        return historial;
    }

    // ------------------------------------------------------------------
    // Helpers internos
    // ------------------------------------------------------------------

    private boolean esVacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    private LocalDate resolverFecha(LocalDate fechaProveida, Cita cita) {
        if (fechaProveida != null) return fechaProveida;
        if (cita.getFechaHora() != null) return cita.getFechaHora().toLocalDate();
        return LocalDate.now();
    }
}
