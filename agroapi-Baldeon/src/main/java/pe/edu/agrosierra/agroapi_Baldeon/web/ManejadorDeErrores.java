package pe.edu.agrosierra.agroapi_Baldeon.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.agrosierra.agroapi_Baldeon.servicio.RecursoNoEncontradoException;
import pe.edu.agrosierra.agroapi_Baldeon.servicio.ConflictoNegocioException;
import pe.edu.agrosierra.agroapi_Baldeon.servicio.ErrorValidacionException;
import pe.edu.agrosierra.agroapi_Baldeon.web.dto.ApiErrorResponse;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

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

    @ExceptionHandler(ErrorValidacionException.class)
    public ResponseEntity<ApiErrorResponse> errorDeCampo(
            ErrorValidacionException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request, exception.getErrores());
    }

    @ExceptionHandler(ConflictoNegocioException.class)
    public ResponseEntity<ApiErrorResponse> conflicto(
            ConflictoNegocioException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> prohibido(
            AccessDeniedException exception, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> validacionDeCampos(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> errores = new TreeMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                errores.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Datos inválidos", request, errores);
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class})
    public ResponseEntity<ApiErrorResponse> parametrosInvalidos(
            Exception exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Solicitud inválida o con formato incorrecto", request, Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> errorInterno(
            Exception exception, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno en el servicio",
                request, Map.of());
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String mensaje,
                                                    HttpServletRequest request,
                                                    Map<String, String> validationErrors) {
        ApiErrorResponse respuesta = new ApiErrorResponse(Instant.now().toString(), status.value(),
                status.getReasonPhrase(), mensaje, request.getRequestURI(), validationErrors);
        return ResponseEntity.status(status).body(respuesta);
    }
}
