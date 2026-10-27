package pe.edu.agrosierra.agroapi_Baldeon.servicio;

public class ConflictoNegocioException extends RuntimeException {

    public ConflictoNegocioException(String mensaje) {
        super(mensaje);
    }
}
