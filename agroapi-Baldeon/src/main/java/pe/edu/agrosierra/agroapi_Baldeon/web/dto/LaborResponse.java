package pe.edu.agrosierra.agroapi_Baldeon.web.dto;

import pe.edu.agrosierra.agroapi_Baldeon.dominio.Labor;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.TipoLabor;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LaborResponse(
        Long id,
        Long loteId,
        TipoLabor tipo,
        LocalDate fecha,
        String responsable,
        BigDecimal cantidad,
        String observacion) {

    public static LaborResponse desde(Labor labor) {
        return new LaborResponse(labor.getId(), labor.getLote().getId(), labor.getTipo(),
                labor.getFecha(), labor.getResponsable().getUsername(), labor.getCantidad(),
                labor.getObservacion());
    }
}
