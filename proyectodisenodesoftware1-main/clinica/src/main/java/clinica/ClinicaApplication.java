package clinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "clinica",
        "controller", "service", "model", "repository", "security", "dto"
})
public class ClinicaApplication {

    // Punto de entrada de la aplicación Spring Boot
    public static void main(String[] args) {
        SpringApplication.run(ClinicaApplication.class, args);
    }

}
