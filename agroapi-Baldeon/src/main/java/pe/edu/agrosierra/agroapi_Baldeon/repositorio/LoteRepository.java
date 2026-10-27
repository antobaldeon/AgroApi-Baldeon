package pe.edu.agrosierra.agroapi_Baldeon.repositorio;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Lote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoteRepository extends JpaRepository<Lote, Long> {

    @Query("""
            select lote from Lote lote
            where (:valle is null or lower(lote.valle) = lower(:valle))
              and (:campaniaCerrada is null or lote.campaniaCerrada = :campaniaCerrada)
            """)
    Page<Lote> buscar(@Param("valle") String valle,
                      @Param("campaniaCerrada") Boolean campaniaCerrada,
                      Pageable pageable);
}
