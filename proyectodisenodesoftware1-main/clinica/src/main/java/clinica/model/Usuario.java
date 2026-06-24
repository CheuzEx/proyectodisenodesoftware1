package clinica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password; // BCrypt

    @Column(nullable = false, length = 30)
    private String rol; // ROLE_ADMIN o ROLE_DOCTOR

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doctor")
    private Doctor doctor; // null si es ROLE_ADMIN

    public Long getId()                      { return id; }
    public void setId(Long id)               { this.id = id; }
    public String getUsername()              { return username; }
    public void setUsername(String u)        { this.username = u; }
    public String getPassword()              { return password; }
    public void setPassword(String p)        { this.password = p; }
    public String getRol()                   { return rol; }
    public void setRol(String rol)           { this.rol = rol; }
    public Doctor getDoctor()                { return doctor; }
    public void setDoctor(Doctor doctor)     { this.doctor = doctor; }
}
