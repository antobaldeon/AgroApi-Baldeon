package pe.edu.agrosierra.agroapi_Baldeon.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/** Carga de forma idempotente los datos necesarios para demostrar el caso AgroSierra. */
@Component
public class DatosSemilla implements ApplicationRunner {

    private static final String CLAVE_PRUEBA = "Agro2026*";

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public DatosSemilla(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        insertarUsuarios();
        insertarRoles();
        insertarPrivilegios();
        asignarRoles();
        asignarPrivilegios();
        insertarLotes();
        insertarLabores();
    }

    private void insertarUsuarios() {
        insertarUsuario(1, "tecnico1", "Técnico de campo");
        insertarUsuario(2, "jefe1", "Jefe de campo");
        insertarUsuario(3, "admin", "Administrador");
    }

    private void insertarUsuario(long id, String username, String nombre) {
        jdbcTemplate.update("""
                INSERT IGNORE INTO usuarios (id, username, password_hash, nombre, activo)
                VALUES (?, ?, ?, ?, TRUE)
                """, id, username, passwordEncoder.encode(CLAVE_PRUEBA), nombre);
    }

    private void insertarRoles() {
        jdbcTemplate.update("""
                INSERT IGNORE INTO roles (id, codigo, nombre) VALUES
                (1, 'TECNICO', 'Técnico de campo'),
                (2, 'JEFE_CAMPO', 'Jefe de campo'),
                (3, 'ADMINISTRADOR', 'Administrador')
                """);
    }

    private void insertarPrivilegios() {
        jdbcTemplate.update("""
                INSERT IGNORE INTO privilegios (id, codigo, descripcion) VALUES
                (1, 'LOTE_LEER', 'Consultar el padrón y el detalle de lotes'),
                (2, 'LABOR_LEER', 'Consultar las labores de un lote'),
                (3, 'LABOR_CREAR', 'Registrar una labor nueva'),
                (4, 'LABOR_EDITAR', 'Corregir labores según las reglas del sistema'),
                (5, 'LABOR_ELIMINAR', 'Eliminar una labor'),
                (6, 'CAMPANIA_CERRAR', 'Cerrar la campaña de un lote'),
                (7, 'USUARIO_GESTIONAR', 'Crear usuarios y asignar roles')
                """);
    }

    private void asignarRoles() {
        jdbcTemplate.update("""
                INSERT IGNORE INTO usuario_rol (usuario_id, rol_id) VALUES
                (1, 1), (2, 2), (3, 3)
                """);
    }

    private void asignarPrivilegios() {
        jdbcTemplate.update("""
                INSERT IGNORE INTO rol_privilegio (rol_id, privilegio_id) VALUES
                (1, 1), (1, 2), (1, 3), (1, 4),
                (2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6),
                (3, 1), (3, 2), (3, 7)
                """);
    }

    private void insertarLotes() {
        List<LoteSemilla> lotes = List.of(
                new LoteSemilla(1, "LOT-001", "La Esperanza", "4.50", "Café", "Valle Alto", true, "2.000"),
                new LoteSemilla(2, "LOT-002", "El Mirador", "6.00", "Maíz", "Valle Alto", true, "1.500"),
                new LoteSemilla(3, "LOT-003", "Santa Rosa", "8.00", "Cacao", "Valle Verde", false, "2.000"),
                new LoteSemilla(4, "LOT-004", "Las Palmas", "5.50", "Café", "Valle Verde", false, "1.800"),
                new LoteSemilla(5, "LOT-005", "El Porvenir", "7.25", "Papa", "Valle Norte", false, "2.200"),
                new LoteSemilla(6, "LOT-006", "Buenavista", "3.75", "Maíz", "Valle Norte", false, "1.600"),
                new LoteSemilla(7, "LOT-007", "La Cosecha", "9.00", "Cacao", "Valle Alto", false, "1.800"),
                new LoteSemilla(8, "LOT-008", "Nuevo Horizonte", "6.50", "Café", "Valle Verde", false, "2.100")
        );

        for (LoteSemilla lote : lotes) {
            jdbcTemplate.update("""
                    INSERT IGNORE INTO lotes
                        (id, codigo, nombre, hectareas, cultivo, valle,
                         campania_cerrada, dosis_maxima_por_hectarea)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """, lote.id(), lote.codigo(), lote.nombre(), new BigDecimal(lote.hectareas()),
                    lote.cultivo(), lote.valle(), lote.campaniaCerrada(),
                    new BigDecimal(lote.dosisMaximaPorHectarea()));
        }
    }

    private void insertarLabores() {
        List<LaborSemilla> labores = List.of(
                new LaborSemilla(1, 1, "RIEGO", "2026-10-18", 1, "1200.000", "Riego de mantenimiento"),
                new LaborSemilla(2, 2, "FUMIGACION", "2026-10-17", 2, "5.500", "Control preventivo"),
                new LaborSemilla(3, 3, "RIEGO", "2026-10-20", 1, "1500.000", "Riego por surcos"),
                new LaborSemilla(4, 3, "FUMIGACION", "2026-10-22", 1, "8.500", "Aplicación dentro de la dosis permitida"),
                new LaborSemilla(5, 3, "COSECHA", "2026-10-26", 2, "320.000", "Cosecha de prueba para cierre"),
                new LaborSemilla(6, 4, "RIEGO", "2026-10-19", 1, "900.000", "Riego programado"),
                new LaborSemilla(7, 4, "FUMIGACION", "2026-10-21", 2, "6.000", "Aplicación preventiva"),
                new LaborSemilla(8, 4, "RIEGO", "2026-10-25", 1, "850.000", "Segundo riego"),
                new LaborSemilla(9, 5, "RIEGO", "2026-10-20", 1, "1100.000", "Riego de campaña"),
                new LaborSemilla(10, 5, "COSECHA", "2026-10-24", 2, "210.000", "Cosecha registrada"),
                new LaborSemilla(11, 5, "FUMIGACION", "2026-10-25", 1, "7.000", "Tratamiento autorizado"),
                new LaborSemilla(12, 6, "RIEGO", "2026-10-18", 1, "700.000", "Riego de parcela"),
                new LaborSemilla(13, 6, "FUMIGACION", "2026-10-22", 2, "4.500", "Control de plaga"),
                new LaborSemilla(14, 6, "RIEGO", "2026-10-26", 1, "750.000", "Riego reciente"),
                new LaborSemilla(15, 7, "RIEGO", "2026-10-21", 1, "1800.000", "Riego de campaña"),
                new LaborSemilla(16, 7, "COSECHA", "2026-10-23", 2, "400.000", "Cosecha registrada"),
                new LaborSemilla(17, 7, "FUMIGACION", "2026-10-26", 1, "10.000", "Aplicación dentro de la dosis permitida"),
                new LaborSemilla(18, 8, "RIEGO", "2026-10-20", 1, "1000.000", "Riego inicial"),
                new LaborSemilla(19, 8, "FUMIGACION", "2026-10-24", 2, "8.000", "Control preventivo"),
                new LaborSemilla(20, 8, "RIEGO", "2026-10-27", 1, "950.000", "Riego reciente")
        );

        for (LaborSemilla labor : labores) {
            jdbcTemplate.update("""
                    INSERT IGNORE INTO labores
                        (id, lote_id, tipo, fecha, responsable_id, cantidad, observacion)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """, labor.id(), labor.loteId(), labor.tipo(), Date.valueOf(LocalDate.parse(labor.fecha())),
                    labor.responsableId(), new BigDecimal(labor.cantidad()), labor.observacion());
        }
    }

    private record LoteSemilla(long id, String codigo, String nombre, String hectareas,
                               String cultivo, String valle, boolean campaniaCerrada,
                               String dosisMaximaPorHectarea) {
    }

    private record LaborSemilla(long id, long loteId, String tipo, String fecha,
                                long responsableId, String cantidad, String observacion) {
    }
}
