package edu.itcr.clinica.controller;

import edu.itcr.clinica.model.Paciente;
import edu.itcr.clinica.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// API REST para la entidad Paciente.
// Base path: /pacientes
@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // GET /pacientes 
    // Lista todos los pacientes ordenados por apellido. 
    @GetMapping
    public List<Paciente> listar() {
        return pacienteService.listar();
    }

    // GET /pacientes/{id} 
    // Obtiene un paciente por ID. 
    @GetMapping("/{id}")
    public Paciente obtener(@PathVariable Long id) {
        return pacienteService.buscarPorId(id);
    }

    // POST /pacientes 
    // Crea un nuevo paciente (y su historial inicial). 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente crear(@Valid @RequestBody Paciente paciente) {
        return pacienteService.crearConHistorial(paciente);
    }

    // PUT /pacientes/{id} 
    // Actualiza los datos de un paciente existente. 
    @PutMapping("/{id}")
    public Paciente actualizar(@PathVariable Long id, @Valid @RequestBody Paciente cambios) {
        return pacienteService.actualizar(id, cambios);
    }

    // DELETE /pacientes/{id} 
    // Elimina un paciente por ID. 
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
    }
}
