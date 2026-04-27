package edu.itcr.clinica.controller;

import edu.itcr.clinica.model.Doctor;
import edu.itcr.clinica.service.DoctorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DoctorService doctorService;

    public DashboardController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Carga un doctor para mostrar datos en el dashboard inicial
        Doctor doctor = doctorService.listar().stream().findFirst().orElse(null);
        model.addAttribute("doctor", doctor);
        return "dashboard";
    }
}
