package pe.edu.agrosierra.agroapi_Baldeon.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuarios_username", columnNames = "username")
})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 80)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "usuario_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "rol_id", nullable = false)
    )
    private Set<Rol> roles = new HashSet<>();

    @OneToMany(mappedBy = "responsable", fetch = FetchType.LAZY)
    private Set<Labor> laboresRegistradas = new HashSet<>();

    protected Usuario() {
        // Constructor requerido por JPA.
    }

    public Usuario(String username, String passwordHash, String nombre) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombre = nombre;
        this.activo = true;
    }

    public void asignarRol(Rol rol) {
        if (rol != null && roles.add(rol)) {
            rol.agregarUsuario(this);
        }
    }

    public void quitarRol(Rol rol) {
        if (rol != null && roles.remove(rol)) {
            rol.quitarUsuario(this);
        }
    }

    void registrarLabor(Labor labor) {
        laboresRegistradas.add(labor);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public Set<Rol> getRoles() {
        return Set.copyOf(roles);
    }

    public Set<Labor> getLaboresRegistradas() {
        return Set.copyOf(laboresRegistradas);
    }
}
