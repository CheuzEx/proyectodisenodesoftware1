package clinica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador que sirve las vistas HTML estáticas (páginas frontend).
 * Todas las rutas redirigen (forward) a archivos .html ubicados en
 * src/main/resources/static/.
 */
@Controller
public class ViewController {

    // Página principal / panel de control
    @GetMapping("/")
    public String dashboard() {
        return "forward:/dashboard.html";
    }

    // Página de inicio de sesión
    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }

    // Vista de listado de pacientes
    @GetMapping("/pacientes/vista")
    public String pacientes() {
        return "forward:/pacientes.html";
    }

    // Vista de historiales médicos (lista de pacientes)
    @GetMapping("/historiales/vista")
    public String listaPacientes() {
        return "forward:/listaPacientes.html";
    }

    // Vista de detalle de un historial médico (con ID en la ruta)
    @GetMapping("/historiales/{id}")
    public String detalleHistorial() {
        return "forward:/detalleHistorial.html";
    }

    // Vista de perfil de doctor (sin ID)
    @GetMapping("/doctor")
    public String doctor() {
        return "forward:/doctor.html";
    }

    // Vista de perfil de doctor (con ID, para mostrar un doctor específico)
    @GetMapping("/doctor/{id}")
    public String doctorPorId() {
        return "forward:/doctor.html";
    }

    // Vista del formulario para editar un doctor (con ID)
    @GetMapping("/doctor/editar/{id}")
    public String doctorForm() {
        return "forward:/doctor_form.html";
    }

    // Vista del formulario para gestionar especialidades de un doctor
    @GetMapping("/doctor/especialidades/{id}")
    public String doctorEspecialidades() {
        return "forward:/doctor_especialidades_form.html";
    }

    // Vista de listado de citas
    @GetMapping("/citas/vista")
    public String citas() {
        return "forward:/citas.html";
    }
}
