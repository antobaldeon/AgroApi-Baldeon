package pe.edu.agrosierra.agroapi_Baldeon.servicio;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Lote;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.TipoLabor;
import pe.edu.agrosierra.agroapi_Baldeon.repositorio.LaborRepository;
import pe.edu.agrosierra.agroapi_Baldeon.repositorio.LoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampaniaService {

    private final LoteRepository loteRepository;
    private final LaborRepository laborRepository;

    public CampaniaService(LoteRepository loteRepository, LaborRepository laborRepository) {
        this.loteRepository = loteRepository;
        this.laborRepository = laborRepository;
    }

    @Transactional
    public Lote cerrar(Long loteId) {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el lote solicitado"));
        if (!laborRepository.existsByLoteIdAndTipo(loteId, TipoLabor.COSECHA)) {
            throw new ConflictoNegocioException("No se puede cerrar la campaña: el lote no tiene labores de cosecha");
        }
        lote.cerrarCampania();
        return lote;
    }
}
