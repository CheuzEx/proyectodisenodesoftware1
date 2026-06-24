package clinica.service;

import clinica.dto.historial.HistorialResponseDTO;
import clinica.dto.historial.RegistrarAtencionRequestDTO;
import clinica.model.Cita;
import java.util.Map;
import clinica.model.HistorialMedico;
import clinica.model.Paciente;
import clinica.model.Receta;
import clinica.repository.CitaRepository;
import clinica.repository.HistorialMedicoRepository;
import clinica.repository.PacienteRepository;
import clinica.repository.RecetaRepository;
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

    // Devuelve todos los historiales médicos mapeados a DTOs.
    @Transactional(readOnly = true)
    public List<HistorialResponseDTO> listar() {
        return historialRepo.findAll().stream()
                .map(HistorialResponseDTO::fromEntity)
                .toList();
    }

    // Devuelve los historiales de un paciente mapeados a DTOs.
    @Transactional(readOnly = true)
    public List<HistorialResponseDTO> listarPorPaciente(Long idPaciente) {
        return historialRepo.findHistorialesRealesPorPaciente(idPaciente).stream()
                .map(h -> {
                    HistorialResponseDTO dto = HistorialResponseDTO.fromEntity(h);

                    if (h.getCita() != null && h.getCita().getIdCita() != null) {
                        dto.setRecetas(
                                recetaRepo.findByCita_IdCita(h.getCita().getIdCita())
                                        .stream()
                                        .map(clinica.dto.receta.RecetaResponseDTO::fromEntity)
                                        .toList()
                        );
                    }

                    return dto;
                })
                .toList();
    }

    // Busca el historial asociado a una cita y lo mapea a DTO.
    @Transactional(readOnly = true)
    public HistorialResponseDTO buscarPorCita(Long idCita) {
        HistorialMedico historial = historialRepo.findByCita_IdCita(idCita)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Historial no encontrado para la cita: " + idCita));
        return HistorialResponseDTO.fromEntity(historial);
    }

    // Devuelve un paciente por ID (mantiene entidad porque el controlador la transforma).
    @Transactional(readOnly = true)
    public Paciente buscarPaciente(Long idPaciente) {
        return pacienteRepo.findById(idPaciente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Paciente no encontrado: " + idPaciente));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarCitasConRecetasPorPaciente(Long idPaciente) {
        return citaRepo.findByPaciente_IdPacienteOrderByFechaHoraDesc(idPaciente)
                .stream()
                .map(cita -> Map.<String, Object>of(
                        "idCita", cita.getIdCita(),
                        "fecha", cita.getFechaHora() != null ? cita.getFechaHora().toLocalDate() : null,
                        "hora", cita.getFechaHora() != null ? cita.getFechaHora().toLocalTime() : null,
                        "estado", cita.getEstado(),
                        "recetas", recetaRepo.findByCita_IdCita(cita.getIdCita())
                                .stream()
                                .map(clinica.dto.receta.RecetaResponseDTO::fromEntity)
                                .toList()
                ))
                .toList();
    }

    /*
      Registra la atención de una cita utilizando el RequestDTO unificado.
     */
    @Transactional
    public HistorialResponseDTO registrarAtencion(RegistrarAtencionRequestDTO dto) {

        if (dto == null || dto.getCitaId() == null || esVacio(dto.getDiagnostico()) || esVacio(dto.getTratamiento())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "citaId, diagnostico y tratamiento son obligatorios.");
        }

        Cita cita = citaRepo.findById(dto.getCitaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cita no encontrada: " + dto.getCitaId()));

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
        historial.setDiagnostico(dto.getDiagnostico().trim());
        historial.setTratamiento(dto.getTratamiento().trim());
        historial.setFechaConsulta(resolverFecha(dto.getFechaConsulta(), cita));

        historial = historialRepo.save(historial);

        // Persistir recetas válidas mapeando desde las recetas del DTO
        List<Receta> recetasCreadas = new ArrayList<>();

        if (dto.getRecetas() != null) {
            for (var r : dto.getRecetas()) {

                if (r == null ||
                        esVacio(r.getMedicamento()) ||
                        esVacio(r.getDosis())) {
                    continue;
                }

                Receta receta = new Receta();
                receta.setHistorial(historial);

                receta.setCita(cita);

                receta.setMedicamento(r.getMedicamento().trim());
                receta.setDosis(r.getDosis().trim());
                receta.setFrecuencia(
                        esVacio(r.getFrecuencia())
                                ? null
                                : r.getFrecuencia().trim()
                );
                receta.setDuracion(r.getDuracion());

                recetasCreadas.add(recetaRepo.save(receta));
            }
        }

        // Marcar cita como atendida
        cita.setEstado(Cita.CitaEstado.ATENDIDA);
        citaRepo.save(cita);

        historial.setRecetas(recetasCreadas);

        // Retornar la respuesta convertida a DTO
        return HistorialResponseDTO.fromEntity(historial);
    }

    // Helpers internos
    private boolean esVacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    private LocalDate resolverFecha(LocalDate fechaProveida, Cita cita) {
        if (fechaProveida != null) return fechaProveida;
        if (cita.getFechaHora() != null) return cita.getFechaHora().toLocalDate();
        return LocalDate.now();
    }
}
