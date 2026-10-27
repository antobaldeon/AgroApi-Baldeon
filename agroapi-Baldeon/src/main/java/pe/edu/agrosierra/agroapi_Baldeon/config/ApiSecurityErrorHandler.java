package pe.edu.agrosierra.agroapi_Baldeon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.ApiErrorResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
public class ApiSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public ApiSecurityErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        escribir(response, 401, "Unauthorized", "Token ausente, inválido o vencido", request.getRequestURI());
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        escribir(response, 403, "Forbidden", "No tienes el privilegio requerido", request.getRequestURI());
    }

    private void escribir(HttpServletResponse response, int status, String error,
                          String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), new ApiErrorResponse(
                Instant.now().toString(), status, error, message, path, Map.of()));
    }
}
