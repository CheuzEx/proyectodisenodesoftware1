package clinica.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad que representa a un médico en el sistema.
 * Mapea la tabla "doctor" del esquema "clinica".
 * Relación muchos a muchos con Especialidad a través de la tabla intermedia "doc_especialidad".
 */
@Entity
@Table(name = "doctor", schema = "clinica")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doctor")
    private Long idDoctor;  // Identificador único del doctor (autogenerado)

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;  // Nombre del doctor

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String apellido;  // Apellido del doctor

    @Column(length = 50)
    private String telefono;  // Número de teléfono (opcional)

    @Column(length = 150)
    private String direccion; // Dirección (opcional)

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "doc_especialidad",
            schema = "clinica",
            joinColumns = @JoinColumn(name = "id_doctor"),
            inverseJoinColumns = @JoinColumn(name = "id_especialidad")
    )
    @JsonIgnore   // Evita la serialización recursiva al obtener doctores
    private Set<Especialidad> especialidades = new HashSet<>(); // Especialidades asociadas al doctor

    // Constructores
    public Doctor() {}

    public Doctor(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }

    // Getters y Setters
    public Long getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(Long idDoctor) {
        this.idDoctor = idDoctor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Set<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(Set<Especialidad> especialidades) {
        this.especialidades = especialidades != null ? especialidades : new HashSet<>();
    }

    // Métodos de utilidad para manejar la relación bidireccional
    public void addEspecialidad(Especialidad especialidad) {
        if (especialidad == null) return;
        this.especialidades.add(especialidad);
        especialidad.getDoctores().add(this);  // Sincroniza el otro lado de la relación
    }

    public void removeEspecialidad(Especialidad especialidad) {
        if (especialidad == null) return;
        this.especialidades.remove(especialidad);
        especialidad.getDoctores().remove(this); // Elimina la referencia bidireccional
    }

    // equals y hashCode basados en el ID (para uso en colecciones)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Doctor doctor = (Doctor) o;
        return Objects.equals(idDoctor, doctor.idDoctor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDoctor);
    }

    // Representación en texto (útil para logs)
    @Override
    public String toString() {
        return "Doctor{" +
                "idDoctor=" + idDoctor +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                '}';
    }
}
