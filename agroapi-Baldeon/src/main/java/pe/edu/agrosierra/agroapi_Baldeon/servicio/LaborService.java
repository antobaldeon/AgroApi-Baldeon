package pe.edu.agrosierra.agroapi_Baldeon.servicio;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Labor;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.Lote;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.Usuario;
import pe.edu.agrosierra.agroapi_Baldeon.repositorio.LaborRepository;
import pe.edu.agrosierra.agroapi_Baldeon.repositorio.LoteRepository;
import pe.edu.agrosierra.agroapi_Baldeon.seguridad.UsuarioRepository;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.ActualizarLaborRequest;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.RegistrarLaborRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        labor.corregir(request.tipo(), request.fecha(), request.cantidad(), request.observacion().trim());
        return labor;
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
