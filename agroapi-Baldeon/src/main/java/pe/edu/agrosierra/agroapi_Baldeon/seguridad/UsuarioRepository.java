package pe.edu.agrosierra.agroapi_Baldeon.seguridad;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = {"roles", "roles.privilegios"})
    Optional<Usuario> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "roles.privilegios"})
    Optional<Usuario> findWithRolesAndPrivilegesById(Long id);
}
