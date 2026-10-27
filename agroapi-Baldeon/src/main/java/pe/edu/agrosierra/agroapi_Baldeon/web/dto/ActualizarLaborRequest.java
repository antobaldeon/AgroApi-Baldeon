package pe.edu.agrosierra.agroapi_Baldeon.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.edu.agrosierra.agroapi_Baldeon.dominio.TipoLabor;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ActualizarLaborRequest(
        @NotNull TipoLabor tipo,
        @NotNull LocalDate fecha,
        @NotNull @DecimalMin("0.0") BigDecimal cantidad,
        @NotBlank @Size(max = 4000) String observacion) {
}
