package pe.edu.agrosierra.agroapi_Baldeon.servicio;

import java.util.Map;

public class ErrorValidacionException extends RuntimeException {

    private final Map<String, String> errores;

    public ErrorValidacionException(String campo, String mensaje) {
        super("Datos inválidos");
        this.errores = Map.of(campo, mensaje);
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
