package pe.edu.agrosierra.agroapi_Baldeon.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lotes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_lotes_codigo", columnNames = "codigo")
}, indexes = {
        @Index(name = "idx_lotes_valle_estado", columnList = "valle, campania_cerrada")
})
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 40)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "hectareas", nullable = false, precision = 10, scale = 2)
    private BigDecimal hectareas;

    @Column(name = "cultivo", nullable = false, length = 100)
    private String cultivo;

    @Column(name = "valle", nullable = false, length = 100)
    private String valle;

    @Column(name = "campania_cerrada", nullable = false)
    private boolean campaniaCerrada;

    @Column(name = "dosis_maxima_por_hectarea", nullable = false, precision = 12, scale = 3)
    private BigDecimal dosisMaximaPorHectarea;

    @OneToMany(mappedBy = "lote", fetch = FetchType.LAZY)
    private Set<Labor> labores = new HashSet<>();

    protected Lote() {
        // Constructor requerido por JPA.
    }

    public Lote(String codigo, String nombre, BigDecimal hectareas, String cultivo,
                String valle, BigDecimal dosisMaximaPorHectarea) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.hectareas = hectareas;
        this.cultivo = cultivo;
        this.valle = valle;
        this.dosisMaximaPorHectarea = dosisMaximaPorHectarea;
        this.campaniaCerrada = false;
    }

    void registrarLabor(Labor labor) {
        labores.add(labor);
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

    public BigDecimal getHectareas() {
        return hectareas;
    }

    public String getCultivo() {
        return cultivo;
    }

    public String getValle() {
        return valle;
    }

    public boolean isCampaniaCerrada() {
        return campaniaCerrada;
    }

    public BigDecimal getDosisMaximaPorHectarea() {
        return dosisMaximaPorHectarea;
    }

    public Set<Labor> getLabores() {
        return Set.copyOf(labores);
    }
}
