package pe.edu.agrosierra.agroapi_Baldeon.web.dto;

import java.util.Map;

/** Formato común de error definido para el contrato del API. */
public record ApiErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors) {
}
