package com.bancoxyz.transaccion.dto;

import com.bancoxyz.transaccion.model.Transaccion;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para transferir datos de Transaccion entre capas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {

    private Long id;

    @NotBlank(message = "El ID de cuenta es obligatorio")
    private String cuentaId;

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate fecha;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto no puede ser negativo")
    private BigDecimal monto;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    private Boolean anomalia;

    public TransaccionDTO(Transaccion transaccion) {
        this.id = transaccion.getId();
        this.cuentaId = transaccion.getCuentaId();
        this.fecha = transaccion.getFecha();
        this.monto = transaccion.getMonto();
        this.tipo = transaccion.getTipo();
        this.descripcion = transaccion.getDescripcion();
        this.anomalia = transaccion.getAnomalia();
    }

    public Transaccion toEntity() {
        Transaccion transaccion = new Transaccion();
        transaccion.setId(this.id);
        transaccion.setCuentaId(this.cuentaId);
        transaccion.setFecha(this.fecha);
        transaccion.setMonto(this.monto);
        transaccion.setTipo(this.tipo);
        transaccion.setDescripcion(this.descripcion);
        transaccion.setAnomalia(this.anomalia != null ? this.anomalia : false);
        return transaccion;
    }
}
