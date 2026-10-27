package pe.edu.agrosierra.agroapi_Baldeon.seguridad;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Privilegio;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.Rol;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.Usuario;
import pe.edu.agrosierra.agroapi_Baldeon.servicio.RecursoNoEncontradoException;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.UsuarioActualResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioActualService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioActualService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public UsuarioActualResponse obtener(String subject) {
        Long id;
        try {
            id = Long.valueOf(subject);
        } catch (NumberFormatException exception) {
            throw new RecursoNoEncontradoException("El usuario del token ya no existe");
        }

        Usuario usuario = usuarioRepository.findWithRolesAndPrivilegesById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario del token ya no existe"));
        List<String> roles = usuario.getRoles().stream()
                .map(Rol::getCodigo).distinct().sorted().toList();
        List<String> privilegios = usuario.getRoles().stream()
                .flatMap(rol -> rol.getPrivilegios().stream())
                .map(Privilegio::getCodigo).distinct().sorted().toList();

        return new UsuarioActualResponse(usuario.getId(), usuario.getUsername(), usuario.getNombre(),
                roles, privilegios);
    }
}
