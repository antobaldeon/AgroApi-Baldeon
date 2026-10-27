package pe.edu.agrosierra.agroapi_Baldeon.seguridad;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.ApiErrorResponse;

import java.time.Instant;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AutenticacionService autenticacionService;

    public AuthController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AutenticacionService.SesionAutenticada sesion =
                autenticacionService.autenticar(request.usuario(), request.contrasena());
        return new LoginResponse(sesion.token(), sesion.usuario(), sesion.nombre(),
                sesion.roles(), sesion.privilegios(), TokenJwtService.VIGENCIA.toSeconds());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> credencialesInvalidas(
            BadCredentialsException exception, jakarta.servlet.http.HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse(Instant.now().toString(), 401, "Unauthorized",
                        "Usuario o contraseña incorrectos", request.getRequestURI(), Map.of()));
    }

    public record LoginRequest(@NotBlank String usuario, @NotBlank String contrasena) {
    }

    public record LoginResponse(String token, String usuario, String nombre,
                                List<String> roles, List<String> privilegios, long expiraEnSegundos) {
    }
}
