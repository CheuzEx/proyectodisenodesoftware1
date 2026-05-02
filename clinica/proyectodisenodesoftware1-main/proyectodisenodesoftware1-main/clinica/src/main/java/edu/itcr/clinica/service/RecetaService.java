package edu.itcr.clinica.service;

import edu.itcr.clinica.model.Receta;
import edu.itcr.clinica.repository.RecetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecetaService {

    private final RecetaRepository recetaRepo;

    public RecetaService(RecetaRepository recetaRepo) {
        this.recetaRepo = recetaRepo;
    }

    // Devuelve todas las recetas registradas. 
    @Transactional(readOnly = true)
    public List<Receta> listar() {
        return recetaRepo.findAll();
    }

    // Persiste una nueva receta. 
    @Transactional
    public Receta crear(Receta receta) {
        return recetaRepo.save(receta);
    }
}
