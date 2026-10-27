package pe.edu.agrosierra.agroapi_Baldeon.repositorio;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Labor;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.TipoLabor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LaborRepository extends JpaRepository<Labor, Long> {

    @EntityGraph(attributePaths = {"lote", "responsable"})
    Page<Labor> findByLoteId(Long loteId, Pageable pageable);

    @EntityGraph(attributePaths = {"lote", "responsable"})
    Optional<Labor> findWithRelationsById(Long id);

    boolean existsByLoteIdAndTipo(Long loteId, TipoLabor tipo);
}
