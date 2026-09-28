package com.bancoxyz.reporte.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaActualizadaEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cuentaId;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoNuevo;
    private BigDecimal montoCambio;
    private String motivo;
    private Long transaccionId;
    private String origen;
}