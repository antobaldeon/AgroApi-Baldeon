package pe.edu.agrosierra.agroapi_Baldeon.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.agrosierra.agroapi_Baldeon.servicio.RecursoNoEncontradoException;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.ApiErrorResponse;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ManejadorDeErrores {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> noEncontrado(
            RecursoNoEncontradoException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> solicitudInvalida(
            IllegalArgumentException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request, Map.of());
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String mensaje,
                                                    HttpServletRequest request,
                                                    Map<String, String> validationErrors) {
        ApiErrorResponse respuesta = new ApiErrorResponse(Instant.now().toString(), status.value(),
                status.getReasonPhrase(), mensaje, request.getRequestURI(), validationErrors);
        return ResponseEntity.status(status).body(respuesta);
    }
}
