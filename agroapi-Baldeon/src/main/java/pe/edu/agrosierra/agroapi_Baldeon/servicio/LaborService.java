package pe.edu.agrosierra.agroapi_Baldeon.servicio;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Labor;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.Lote;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.TipoLabor;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.Usuario;
import pe.edu.agrosierra.agroapi_Baldeon.repositorio.LaborRepository;
import pe.edu.agrosierra.agroapi_Baldeon.repositorio.LoteRepository;
import pe.edu.agrosierra.agroapi_Baldeon.seguridad.UsuarioRepository;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.ActualizarLaborRequest;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.RegistrarLaborRequest;
import pe.edu.agrosierra.agroapi_Baldeon.seguridad.CodigosPrivilegio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Service
public class LaborService {

    private final LaborRepository laborRepository;
    private final LoteRepository loteRepository;
    private final UsuarioRepository usuarioRepository;

    public LaborService(LaborRepository laborRepository,
                        LoteRepository loteRepository,
                        UsuarioRepository usuarioRepository) {
        this.laborRepository = laborRepository;
        this.loteRepository = loteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Page<Labor> listarPorLote(Long loteId, int pagina, int tamanio) {
        if (!loteRepository.existsById(loteId)) {
            throw new RecursoNoEncontradoException("No existe el lote solicitado");
        }
        Sort sort = Sort.by(Sort.Order.desc("fecha"), Sort.Order.desc("id"));
        return laborRepository.findByLoteId(loteId, LoteService.pageable(pagina, tamanio, sort));
    }

    @Transactional
    public Labor registrar(RegistrarLaborRequest request, Long responsableId) {
        Lote lote = loteRepository.findById(request.loteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el lote solicitado"));
        validarCampaniaAbierta(lote);
        validarDosis(lote, request.tipo(), request.cantidad());
        Usuario responsable = usuarioRepository.findById(responsableId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario autenticado ya no existe"));
        Labor labor = new Labor(lote, request.tipo(), request.fecha(), responsable,
                request.cantidad(), request.observacion().trim());
        return laborRepository.save(labor);
    }

    @Transactional
    public Labor actualizar(Long id, ActualizarLaborRequest request, Long usuarioId,
                            Authentication authentication) {
        Labor labor = buscar(id);
        validarCampaniaAbierta(labor.getLote());
        if (!tienePrivilegio(authentication, CodigosPrivilegio.LABOR_EDITAR_TODAS)) {
            if (!Objects.equals(labor.getResponsable().getId(), usuarioId)) {
                throw new AccessDeniedException("Solo puedes corregir las labores que registraste");
            }
            Instant ahora = Instant.now();
            Instant limite = labor.getRegistradoEn().plus(Duration.ofHours(24));
            if (ahora.isBefore(labor.getRegistradoEn()) || ahora.isAfter(limite)) {
                throw new AccessDeniedException("El plazo de 24 horas para corregir esta labor venció");
            }
        }
        validarDosis(labor.getLote(), request.tipo(), request.cantidad());
        labor.corregir(request.tipo(), request.fecha(), request.cantidad(), request.observacion().trim());
        return labor;
    }

    private void validarCampaniaAbierta(Lote lote) {
        if (lote.isCampaniaCerrada()) {
            throw new ConflictoNegocioException("No se puede registrar ni corregir labores: la campaña del lote está cerrada");
        }
    }

    private void validarDosis(Lote lote, TipoLabor tipo, BigDecimal cantidad) {
        if (tipo == TipoLabor.FUMIGACION) {
            BigDecimal dosisTotalMaxima = lote.getDosisMaximaPorHectarea().multiply(lote.getHectareas());
            if (cantidad.compareTo(dosisTotalMaxima) > 0) {
                throw new ErrorValidacionException("cantidad",
                        "La cantidad supera la dosis máxima del lote (" + dosisTotalMaxima.stripTrailingZeros().toPlainString() + ")");
            }
        }
    }

    private boolean tienePrivilegio(Authentication authentication, String privilegio) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(privilegio));
    }

    @Transactional
    public void eliminar(Long id) {
        Labor labor = buscar(id);
        laborRepository.delete(labor);
    }

    @Transactional(readOnly = true)
    public Labor buscar(Long id) {
        return laborRepository.findWithRelationsById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la labor solicitada"));
    }
}
