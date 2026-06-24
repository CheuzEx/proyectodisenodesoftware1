package clinica.service;

import clinica.dto.receta.RecetaRequestDTO;
import clinica.dto.receta.RecetaResponseDTO;
import clinica.model.Cita;
import clinica.model.HistorialMedico;
import clinica.model.Receta;
import clinica.repository.CitaRepository;
import clinica.repository.HistorialMedicoRepository;
import clinica.repository.RecetaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RecetaService {

    private final RecetaRepository recetaRepo;
    private final HistorialMedicoRepository historialRepo;
    private final CitaRepository citaRepo;

    public RecetaService(
            RecetaRepository recetaRepo,
            HistorialMedicoRepository historialRepo,
            CitaRepository citaRepo
    ) {
        this.recetaRepo = recetaRepo;
        this.historialRepo = historialRepo;
        this.citaRepo = citaRepo;
    }

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> listar() {
        return recetaRepo.findAll().stream()
                .map(RecetaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> listarPorCita(Long citaId) {
        return recetaRepo.findByCita_IdCita(citaId).stream()
                .map(RecetaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public RecetaResponseDTO crear(RecetaRequestDTO dto) {

        Cita cita = citaRepo.findById(dto.getCitaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cita no encontrada: " + dto.getCitaId()
                ));

        Long pacienteId = cita.getPaciente().getIdPaciente();

        HistorialMedico historial = historialRepo
                .findByPaciente_IdPaciente(pacienteId)
                .orElseGet(() -> {
                    HistorialMedico nuevo = new HistorialMedico();
                    nuevo.setPaciente(cita.getPaciente());
                    return historialRepo.save(nuevo);
                });

        Receta receta = new Receta();
        receta.setMedicamento(dto.getMedicamento());
        receta.setDosis(dto.getDosis());
        receta.setFrecuencia(dto.getFrecuencia());
        receta.setDuracion(dto.getDuracion());
        receta.setHistorial(historial);
        receta.setCita(cita);

        return RecetaResponseDTO.fromEntity(recetaRepo.save(receta));
    }
}