package edu.itcr.clinica.controller;

import edu.itcr.clinica.model.Doctor;
import edu.itcr.clinica.model.Especialidad;
import edu.itcr.clinica.model.HistorialMedico;
import edu.itcr.clinica.model.Paciente;
import edu.itcr.clinica.service.DoctorService;
import edu.itcr.clinica.service.HistorialMedicoService;
import edu.itcr.clinica.service.PacienteService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlador exclusivo para vistas Thymeleaf.
 * Centraliza todas las rutas que devuelven HTML,
 * separando responsabilidades de los controladores REST.
 */
@Controller
public class VistaController {

    private final DoctorService doctorService;
    private final PacienteService pacienteService;
    private final HistorialMedicoService historialService;

    public VistaController(DoctorService doctorService,
                           PacienteService pacienteService,
                           HistorialMedicoService historialService) {
        this.doctorService  = doctorService;
        this.pacienteService = pacienteService;
        this.historialService = historialService;
    }

    // -------------------------------------------------------------------------
    // Vistas de Citas e Historial (sin lógica de datos extra)
    // -------------------------------------------------------------------------

    @GetMapping("/citas/vista")
    public String citas() {
        return "citas";
    }

    @GetMapping("/historial/vista")
    public String historial() {
        return "historial";
    }

    // -------------------------------------------------------------------------
    // Vistas de Doctor
    // -------------------------------------------------------------------------

    @GetMapping("/doctor")
    public String doctorRoot() {
        Long id = doctorService.listar().stream()
                .map(Doctor::getIdDoctor)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay doctores registrados"));
        return "redirect:/doctor/" + id;
    }

    @GetMapping("/doctor/{id}")
    public String doctorVer(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorService.buscarPorId(id));
        return "doctor";
    }

    @GetMapping("/doctor/editar/{id}")
    public String doctorEditar(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorService.buscarPorId(id));
        return "doctor_form";
    }

    @PostMapping("/doctor/guardar")
    public String doctorGuardar(@ModelAttribute("doctor") Doctor form) {
        Doctor actualizado = doctorService.actualizar(form.getIdDoctor(), form);
        return "redirect:/doctor/" + actualizado.getIdDoctor();
    }

    @GetMapping("/doctor/especialidades/{id}")
    public String doctorEditarEspecialidades(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.buscarPorId(id);
        List<Especialidad> todas = doctorService.listarEspecialidades();
        Set<Long> actuales = doctor.getEspecialidades().stream()
                .map(Especialidad::getIdEspecialidad)
                .collect(Collectors.toSet());
        model.addAttribute("doctor",              doctor);
        model.addAttribute("todasEspecialidades", todas);
        model.addAttribute("idsActuales",          actuales);
        return "doctor_especialidades_form";
    }

    @PostMapping("/doctor/especialidades/{id}")
    public String doctorGuardarEspecialidades(
            @PathVariable Long id,
            @RequestParam(name = "especialidadesIds", required = false) List<Long> especialidadIds,
            RedirectAttributes ra) {
        doctorService.actualizarEspecialidades(id, especialidadIds);
        ra.addFlashAttribute("ok", "Especialidades actualizadas correctamente.");
        return "redirect:/doctor/" + id;
    }

    @PostMapping("/doctor/especialidades/{id}/crear")
    public String doctorCrearEspecialidad(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            RedirectAttributes ra) {
        try {
            doctorService.crearEspecialidadSiNoExiste(nombre);
            ra.addFlashAttribute("okEsp", "Especialidad creada / disponible.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorEsp", e.getMessage());
        }
        return "redirect:/doctor/especialidades/" + id;
    }

    // -------------------------------------------------------------------------
    // Vistas de Pacientes
    // -------------------------------------------------------------------------

    @GetMapping("/pacientes/vista")
    public String pacientesVista(
            @RequestParam(value = "edit",   required = false) Long editId,
            @RequestParam(value = "id",     required = false) Long searchId,
            Model model) {

        if (searchId != null) {
            try {
                model.addAttribute("pacientes", List.of(pacienteService.buscarPorId(searchId)));
            } catch (Exception e) {
                model.addAttribute("pacientes", List.of());
                model.addAttribute("mensaje", "No se encontró el paciente con ID " + searchId);
            }
            model.addAttribute("searchId", searchId);
        } else {
            model.addAttribute("pacientes", pacienteService.listar());
        }

        if (editId != null) {
            try {
                model.addAttribute("pacienteEdit", pacienteService.buscarPorId(editId));
            } catch (Exception ignored) {}
        }
        return "pacientes";
    }

    // -------------------------------------------------------------------------
    // Vistas de Historial Médico
    // -------------------------------------------------------------------------

    @GetMapping("/historiales/vista")
    public String historialListaPacientes(
            @RequestParam(value = "q", required = false) String q,
            Model model) {
        model.addAttribute("pacientes", pacienteService.buscar(q));
        model.addAttribute("q", q);
        return "listaPacientes";
    }

    @GetMapping("/historiales/{idPaciente}")
    @Transactional(readOnly = true)
    public String historialPorPaciente(@PathVariable Long idPaciente, Model model) {
        Paciente paciente            = historialService.buscarPaciente(idPaciente);
        List<HistorialMedico> historiales = historialService.listarPorPaciente(idPaciente);
        model.addAttribute("paciente",    paciente);
        model.addAttribute("historiales", historiales);
        return "detalleHistorial";
    }

    // -------------------------------------------------------------------------
    // Endpoint JSON para especialidades de doctor (usado por el frontend)
    // -------------------------------------------------------------------------

    @GetMapping(value = "/doctor/{id}/especialidades.json")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<DoctorController.EspecialidadDto> especialidadesJson(@PathVariable Long id) {
        Collator collator = Collator.getInstance(new Locale("es", "CR"));
        return doctorService.especialidadesPorDoctor(id).stream()
                .sorted((a, b) -> collator.compare(a.getNomEspecialidad(), b.getNomEspecialidad()))
                .map(e -> new DoctorController.EspecialidadDto(e.getIdEspecialidad(), e.getNomEspecialidad()))
                .toList();
    }
}
