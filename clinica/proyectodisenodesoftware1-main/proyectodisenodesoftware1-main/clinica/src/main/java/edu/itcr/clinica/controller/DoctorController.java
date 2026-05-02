package edu.itcr.clinica.controller;

import edu.itcr.clinica.model.Doctor;
import edu.itcr.clinica.model.Especialidad;
import edu.itcr.clinica.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//API REST para la entidad Doctor.
//Base path: /api/doctores
@RestController
@RequestMapping("/api/doctores")
@CrossOrigin
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // GET /api/doctores  
    //Lista todos los doctores.
    @GetMapping
    public List<Doctor> listar() {
        return doctorService.listar();
    }

    // GET /api/doctores/{id}  
    // Obtiene un doctor por ID. 
    @GetMapping("/{id}")
    public Doctor obtener(@PathVariable Long id) {
        return doctorService.buscarPorId(id);
    }

    //POST /api/doctores 
    //Crea un nuevo doctor.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doctor crear(@Valid @RequestBody Doctor doctor) {
        return doctorService.crear(doctor);
    }

    // PUT /api/doctores/{id} 
    // Actualiza los datos de un doctor existente. 
    @PutMapping("/{id}")
    public Doctor actualizar(@PathVariable Long id, @Valid @RequestBody Doctor cambios) {
        return doctorService.actualizar(id, cambios);
    }

    // DELETE /api/doctores/{id} 
    // Elimina un doctor por ID. 
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        doctorService.eliminar(id);
    }

    // GET /api/doctores/{id}/especialidades 
    // Devuelve las especialidades de un doctor. 
    @GetMapping("/{id}/especialidades")
    public List<Especialidad> obtenerEspecialidades(@PathVariable Long id) {
        return doctorService.especialidadesPorDoctor(id);
    }


    // PUT /api/doctores/{id}/especialidades
    // Reemplaza el conjunto completo de especialidades del doctor.
    @PutMapping("/{id}/especialidades")
    public Doctor actualizarEspecialidades(@PathVariable Long id,
                                           @RequestBody List<Long> especialidadIds) {
        return doctorService.actualizarEspecialidades(id, especialidadIds);
    }

    // DTO para respuestas de especialidades
    public record EspecialidadDto(Long idEspecialidad, String nombre) {}
}
