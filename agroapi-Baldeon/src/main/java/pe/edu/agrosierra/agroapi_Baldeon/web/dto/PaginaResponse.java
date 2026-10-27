package pe.edu.agrosierra.agroapi_Baldeon.web.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PaginaResponse<T>(
        List<T> contenido,
        int pagina,
        int tamanio,
        long totalElementos,
        int totalPaginas,
        boolean ultima) {

    public static <S, T> PaginaResponse<T> desde(Page<S> page, Function<S, T> convertidor) {
        return new PaginaResponse<>(page.getContent().stream().map(convertidor).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isLast());
    }
}
