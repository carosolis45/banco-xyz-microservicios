package com.bancoxyz.reporte.dto;

import com.bancoxyz.reporte.model.Reporte;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para transferir datos de Reporte entre capas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDTO {

    private Long id;

    @NotBlank(message = "El ID de cuenta es obligatorio")
    private String cuentaId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto no puede ser negativo")
    private BigDecimal monto;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    public ReporteDTO(Reporte reporte) {
        this.id = reporte.getId();
        this.cuentaId = reporte.getCuentaId();
        this.fecha = reporte.getFecha();
        this.monto = reporte.getMonto();
        this.tipo = reporte.getTipo();
        this.descripcion = reporte.getDescripcion();
    }

    public Reporte toEntity() {
        Reporte reporte = new Reporte();
        reporte.setId(this.id);
        reporte.setCuentaId(this.cuentaId);
        reporte.setFecha(this.fecha);
        reporte.setMonto(this.monto);
        reporte.setTipo(this.tipo);
        reporte.setDescripcion(this.descripcion);
        return reporte;
    }
}
