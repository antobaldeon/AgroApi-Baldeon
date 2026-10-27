package pe.edu.agrosierra.agroapi_Baldeon.web.dto;

import java.util.List;

public record UsuarioActualResponse(
        Long id,
        String usuario,
        String nombre,
        List<String> roles,
        List<String> privilegios) {
}
