package clinica.controller;

import clinica.dto.receta.RecetaRequestDTO;
import clinica.dto.receta.RecetaResponseDTO;
import clinica.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar recetas médicas.
 * Permite listar, consultar por cita y crear nuevas recetas.
 */
@RestController
@RequestMapping("/api/recetas")
@CrossOrigin
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    // Obtiene todas las recetas (listado completo)
    @GetMapping
    public List<RecetaResponseDTO> listar() {
        return recetaService.listar();
    }

    // Obtiene las recetas asociadas a una cita específica
    @GetMapping("/cita/{citaId}")
    public List<RecetaResponseDTO> listarPorCita(@PathVariable Long citaId) {
        return recetaService.listarPorCita(citaId);
    }

    // Crea una nueva receta asociada a un historial médico
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecetaResponseDTO crear(@Valid @RequestBody RecetaRequestDTO dto) {
        return recetaService.crear(dto);
    }
}
