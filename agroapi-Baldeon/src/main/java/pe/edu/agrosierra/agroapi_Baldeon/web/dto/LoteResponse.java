package pe.edu.agrosierra.agroapi_Baldeon.web.dto;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Lote;

import java.math.BigDecimal;

public record LoteResponse(
        Long id,
        String codigo,
        String nombre,
        BigDecimal hectareas,
        String cultivo,
        String valle,
        boolean campaniaCerrada,
        BigDecimal dosisMaximaPorHectarea) {

    public static LoteResponse desde(Lote lote) {
        return new LoteResponse(lote.getId(), lote.getCodigo(), lote.getNombre(), lote.getHectareas(),
                lote.getCultivo(), lote.getValle(), lote.isCampaniaCerrada(),
                lote.getDosisMaximaPorHectarea());
    }
}
