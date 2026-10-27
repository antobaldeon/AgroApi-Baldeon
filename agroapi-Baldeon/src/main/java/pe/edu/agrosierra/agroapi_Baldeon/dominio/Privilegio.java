package pe.edu.agrosierra.agroapi_Baldeon.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "privilegios", uniqueConstraints = {
        @UniqueConstraint(name = "uk_privilegios_codigo", columnNames = "codigo")
})
public class Privilegio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 80)
    private String codigo;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @ManyToMany(mappedBy = "privilegios", fetch = FetchType.LAZY)
    private Set<Rol> roles = new HashSet<>();

    protected Privilegio() {
        // Constructor requerido por JPA.
    }

    public Privilegio(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    void agregarRol(Rol rol) {
        roles.add(rol);
    }

    void quitarRol(Rol rol) {
        roles.remove(rol);
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Set<Rol> getRoles() {
        return Set.copyOf(roles);
    }
}
