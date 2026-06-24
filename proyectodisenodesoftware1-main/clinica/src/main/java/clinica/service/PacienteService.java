package clinica.service;

import clinica.dto.paciente.PacienteDetalleDTO;
import clinica.dto.paciente.PacienteRequestDTO;
import clinica.dto.paciente.PacienteResumenDTO;
import clinica.model.HistorialMedico;
import clinica.model.Paciente;
import clinica.repository.CitaRepository;
import clinica.repository.HistorialMedicoRepository;
import clinica.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepo;
    private final HistorialMedicoRepository historialRepo;
    private final CitaRepository citaRepo;

    public PacienteService(PacienteRepository pacienteRepo,
                           HistorialMedicoRepository historialRepo,
                           CitaRepository citaRepo) {
        this.pacienteRepo  = pacienteRepo;
        this.historialRepo = historialRepo;
        this.citaRepo      = citaRepo;
    }

    // Mapea la lista de entidades a una lista de Resumen DTOs
    @Transactional(readOnly = true)
    public List<PacienteResumenDTO> listar() {
        return pacienteRepo.findAllByOrderByApellidoAscNombreAsc().stream()
                .map(PacienteResumenDTO::fromEntity)
                .toList();
    }

    // Retorna lista de entidades por si se usa internamente
    @Transactional(readOnly = true)
    public List<Paciente> buscar(String q) {
        if (q == null || q.isBlank()) {
            return pacienteRepo.findAllByOrderByApellidoAscNombreAsc();
        }
        return pacienteRepo.searchByNombreOrApellido(q.trim());
    }

    // Devuelve el Detalle DTO del paciente solicitado
    @Transactional(readOnly = true)
    public PacienteDetalleDTO buscarPorId(Long id) {
        Paciente paciente = pacienteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Paciente no encontrado: " + id));
        return PacienteDetalleDTO.fromEntity(paciente);
    }

    // Helper interno para obtener la entidad pura
    private Paciente buscarEntidadPorId(Long id) {
        return pacienteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Paciente no encontrado: " + id));
    }

    // Crea un paciente nuevo con su historial médico inicial vacío
    @Transactional
    public PacienteDetalleDTO crearConHistorial(PacienteRequestDTO dto) {
        Paciente paciente = new Paciente();
        paciente.setNombre(dto.getNombre());
        paciente.setApellido(dto.getApellido());
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setSexo(dto.getSexo());
        paciente.setDireccion(dto.getDireccion());
        paciente.setTelefono(dto.getTelefono());
        paciente.setCorreo(dto.getCorreo());

        Paciente guardado = pacienteRepo.save(paciente);

        // Historial inicial vacío
        HistorialMedico historialInicial = new HistorialMedico();
        historialInicial.setPaciente(guardado);
        historialRepo.save(historialInicial);

        return PacienteDetalleDTO.fromEntity(guardado);
    }

    // Actualiza los datos personales de un paciente existente
    @Transactional
    public PacienteDetalleDTO actualizar(Long id, PacienteRequestDTO cambios) {
        Paciente paciente = buscarEntidadPorId(id);

        paciente.setNombre(cambios.getNombre());
        paciente.setApellido(cambios.getApellido());
        paciente.setFechaNacimiento(cambios.getFechaNacimiento());
        paciente.setSexo(cambios.getSexo());
        paciente.setDireccion(cambios.getDireccion());
        paciente.setTelefono(cambios.getTelefono());
        paciente.setCorreo(cambios.getCorreo());

        Paciente actualizado = pacienteRepo.save(paciente);
        return PacienteDetalleDTO.fromEntity(actualizado);
    }

    // Elimina un paciente por ID.
    @Transactional
    public void eliminar(Long id) {
        Paciente paciente = pacienteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Paciente no encontrado: " + id));

        boolean tieneCitasProgramadas =
                citaRepo.existsByPaciente_IdPacienteAndEstado(
                        id,
                        clinica.model.Cita.CitaEstado.PROGRAMADA
                );

        if (tieneCitasProgramadas) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar el paciente porque tiene citas programadas.");
        }

        historialRepo.deleteByPaciente_IdPaciente(id);
        citaRepo.deleteByPaciente_IdPaciente(id);
        pacienteRepo.delete(paciente);
    }
}