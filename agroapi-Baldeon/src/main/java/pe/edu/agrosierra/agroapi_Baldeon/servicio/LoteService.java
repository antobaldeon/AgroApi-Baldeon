package pe.edu.agrosierra.agroapi_Baldeon.servicio;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Lote;
import pe.edu.agrosierra.agroapi_Baldeon.repositorio.LoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoteService {

    private static final int TAMANIO_MAXIMO = 100;

    private final LoteRepository loteRepository;

    public LoteService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    @Transactional(readOnly = true)
    public Page<Lote> listar(int pagina, int tamanio, String valle, Boolean campaniaCerrada) {
        Pageable pageable = pageable(pagina, tamanio, Sort.by(Sort.Direction.ASC, "codigo"));
        String valleNormalizado = valle == null || valle.isBlank() ? null : valle.trim();
        return loteRepository.buscar(valleNormalizado, campaniaCerrada, pageable);
    }

    @Transactional(readOnly = true)
    public Lote obtener(Long id) {
        return loteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el lote solicitado"));
    }

    public static Pageable pageable(int pagina, int tamanio, Sort sort) {
        if (pagina < 0) {
            throw new IllegalArgumentException("pagina debe ser mayor o igual a cero");
        }
        if (tamanio < 1 || tamanio > TAMANIO_MAXIMO) {
            throw new IllegalArgumentException("tamanio debe estar entre 1 y " + TAMANIO_MAXIMO);
        }
        return PageRequest.of(pagina, tamanio, sort);
    }
}
