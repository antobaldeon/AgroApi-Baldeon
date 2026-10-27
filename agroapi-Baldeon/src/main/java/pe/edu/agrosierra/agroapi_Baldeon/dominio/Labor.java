package pe.edu.agrosierra.agroapi_Baldeon.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "labores", indexes = {
        @Index(name = "idx_labores_lote_fecha", columnList = "lote_id, fecha"),
        @Index(name = "idx_labores_responsable_fecha", columnList = "responsable_id, fecha")
})
public class Labor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoLabor tipo;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsable_id", nullable = false)
    private Usuario responsable;

    @Column(name = "cantidad", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "observacion", nullable = false, columnDefinition = "TEXT")
    private String observacion;

    protected Labor() {
        // Constructor requerido por JPA.
    }

    public Labor(Lote lote, TipoLabor tipo, LocalDate fecha, Usuario responsable,
                 BigDecimal cantidad, String observacion) {
        this.lote = lote;
        this.tipo = tipo;
        this.fecha = fecha;
        this.responsable = responsable;
        this.cantidad = cantidad;
        this.observacion = observacion;
        if (lote != null) {
            lote.registrarLabor(this);
        }
        if (responsable != null) {
            responsable.registrarLabor(this);
        }
    }

    public Long getId() {
        return id;
    }

    public Lote getLote() {
        return lote;
    }

    public TipoLabor getTipo() {
        return tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public String getObservacion() {
        return observacion;
    }
}
