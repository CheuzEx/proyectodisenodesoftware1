package edu.itcr.clinica.service;

import edu.itcr.clinica.model.HistorialMedico;
import edu.itcr.clinica.model.Paciente;
import edu.itcr.clinica.repository.HistorialMedicoRepository;
import edu.itcr.clinica.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepo;
    private final HistorialMedicoRepository historialRepo;

    public PacienteService(PacienteRepository pacienteRepo,
                           HistorialMedicoRepository historialRepo) {
        this.pacienteRepo  = pacienteRepo;
        this.historialRepo = historialRepo;
    }

    /** Devuelve todos los pacientes ordenados por apellido y nombre. */
    @Transactional(readOnly = true)
    public List<Paciente> listar() {
        return pacienteRepo.findAllByOrderByApellidoAscNombreAsc();
    }

    /**
     * Busca pacientes cuyo nombre o apellido contenga la cadena indicada.
     * Si la cadena está vacía devuelve el listado completo.
     */
    @Transactional(readOnly = true)
    public List<Paciente> buscar(String q) {
        if (q == null || q.isBlank()) return listar();
        return pacienteRepo.searchByNombreOrApellido(q.trim());
    }

    /** Busca un paciente por ID, lanzando 404 si no existe. */
    @Transactional(readOnly = true)
    public Paciente buscarPorId(Long id) {
        return pacienteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Paciente no encontrado: " + id));
    }

    /** Persiste un nuevo paciente sin historial. */
    @Transactional
    public Paciente crear(Paciente paciente) {
        return pacienteRepo.save(paciente);
    }

    /**
     * Persiste un nuevo paciente y crea automáticamente su historial médico inicial vacío.
     * Este es el método que deben usar los endpoints REST (CU-01).
     */
    @Transactional
    public Paciente crearConHistorial(Paciente paciente) {
        Paciente guardado = pacienteRepo.save(paciente);

        HistorialMedico historialInicial = new HistorialMedico();
        historialInicial.setPaciente(guardado);
        historialRepo.save(historialInicial);

        return guardado;
    }

    /** Actualiza los datos permitidos de un paciente existente. */
    @Transactional
    public Paciente actualizar(Long id, Paciente cambios) {
        Paciente paciente = buscarPorId(id);
        paciente.setNombre(cambios.getNombre());
        paciente.setApellido(cambios.getApellido());
        paciente.setFechaNacimiento(cambios.getFechaNacimiento());
        paciente.setSexo(cambios.getSexo());
        paciente.setDireccion(cambios.getDireccion());
        paciente.setTelefono(cambios.getTelefono());
        paciente.setCorreo(cambios.getCorreo());
        return pacienteRepo.save(paciente);
    }

    /** Elimina un paciente por ID. */
    @Transactional
    public void eliminar(Long id) {
        pacienteRepo.deleteById(id);
    }
}
