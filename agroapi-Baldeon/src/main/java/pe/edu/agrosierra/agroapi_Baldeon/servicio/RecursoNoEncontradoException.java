package pe.edu.agrosierra.agroapi_Baldeon.servicio;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
