package clinica.controller;

import clinica.dto.receta.RecetaRequestDTO;
import clinica.dto.receta.RecetaResponseDTO;
import clinica.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@CrossOrigin
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @GetMapping
    public List<RecetaResponseDTO> listar() {
        return recetaService.listar();
    }

    @GetMapping("/cita/{citaId}")
    public List<RecetaResponseDTO> listarPorCita(@PathVariable Long citaId) {
        return recetaService.listarPorCita(citaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecetaResponseDTO crear(@Valid @RequestBody RecetaRequestDTO dto) {
        return recetaService.crear(dto);
    }
}