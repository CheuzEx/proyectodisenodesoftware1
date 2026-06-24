package clinica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Sirve los archivos HTML estáticos ubicados en src/main/resources/static/
 * Cada método mapea una ruta del navegador a su archivo HTML correspondiente.
 */
@Controller
public class ViewController {

    @GetMapping("/")
    public String dashboard() {
        return "forward:/dashboard.html";
    }

    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping("/pacientes/vista")
    public String pacientes() {
        return "forward:/pacientes.html";
    }

    @GetMapping("/historiales/vista")
    public String listaPacientes() {
        return "forward:/listaPacientes.html";
    }

    @GetMapping("/historiales/{id}")
    public String detalleHistorial() {
        return "forward:/detalleHistorial.html";
    }

    @GetMapping("/doctor")
    public String doctor() {
        return "forward:/doctor.html";
    }

    @GetMapping("/doctor/{id}")
    public String doctorPorId() {
        return "forward:/doctor.html";
    }

    @GetMapping("/doctor/editar/{id}")
    public String doctorForm() {
        return "forward:/doctor_form.html";
    }

    @GetMapping("/doctor/especialidades/{id}")
    public String doctorEspecialidades() {
        return "forward:/doctor_especialidades_form.html";
    }

    @GetMapping("/citas/vista")
    public String citas() {
        return "forward:/citas.html";
    }
}
