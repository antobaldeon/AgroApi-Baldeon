package pe.edu.agrosierra.agroapi_Baldeon.seguridad;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Privilegio;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.Rol;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.Usuario;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutenticacionService {

    private static final String MENSAJE_CREDENCIALES_INVALIDAS = "Usuario o contraseña incorrectos";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenJwtService tokenJwtService;

    public AutenticacionService(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder,
                                TokenJwtService tokenJwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenJwtService = tokenJwtService;
    }

    @Transactional(readOnly = true)
    public SesionAutenticada autenticar(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .filter(Usuario::isActivo)
                .filter(encontrado -> passwordEncoder.matches(password, encontrado.getPasswordHash()))
                .orElseThrow(() -> new BadCredentialsException(MENSAJE_CREDENCIALES_INVALIDAS));

        List<String> roles = usuario.getRoles().stream()
                .map(Rol::getCodigo)
                .distinct()
                .sorted()
                .toList();

        List<String> privilegios = usuario.getRoles().stream()
                .flatMap(rol -> rol.getPrivilegios().stream())
                .map(Privilegio::getCodigo)
                .distinct()
                .sorted()
                .toList();

        String token = tokenJwtService.emitir(usuario, roles, privilegios);
        return new SesionAutenticada(token, usuario.getUsername(), usuario.getNombre(), roles, privilegios);
    }

    public record SesionAutenticada(String token, String usuario, String nombre,
                                   List<String> roles, List<String> privilegios) {
    }
}
