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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_roles_codigo", columnNames = "codigo")
})
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 60)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rol_privilegio",
            joinColumns = @JoinColumn(name = "rol_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "privilegio_id", nullable = false)
    )
    private Set<Privilegio> privilegios = new HashSet<>();

    protected Rol() {
        // Constructor requerido por JPA.
    }

    public Rol(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public void agregarPrivilegio(Privilegio privilegio) {
        if (privilegio != null && privilegios.add(privilegio)) {
            privilegio.agregarRol(this);
        }
    }

    public void quitarPrivilegio(Privilegio privilegio) {
        if (privilegio != null && privilegios.remove(privilegio)) {
            privilegio.quitarRol(this);
        }
    }

    void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    void quitarUsuario(Usuario usuario) {
        usuarios.remove(usuario);
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Set<Usuario> getUsuarios() {
        return Set.copyOf(usuarios);
    }

    public Set<Privilegio> getPrivilegios() {
        return Set.copyOf(privilegios);
    }
}
