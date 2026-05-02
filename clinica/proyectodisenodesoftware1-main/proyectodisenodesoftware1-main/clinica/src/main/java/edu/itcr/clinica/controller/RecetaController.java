package edu.itcr.clinica.controller;

import edu.itcr.clinica.model.Receta;
import edu.itcr.clinica.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// API REST para la entidad Receta.
// Base path: /recetas
@RestController
@RequestMapping("/api/recetas")
@CrossOrigin
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    // GET /recetas 
    // Lista todas las recetas registradas. 
    @GetMapping
    public List<Receta> listar() {
        return recetaService.listar();
    }

    // POST /recetas 
    // Crea una nueva receta. 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Receta crear(@Valid @RequestBody Receta receta) {
        return recetaService.crear(receta);
    }
}
