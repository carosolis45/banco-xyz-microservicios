package com.bancoxyz.cuenta.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Evento que se publica cuando se actualiza el saldo de una cuenta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaActualizadaEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cuentaId;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoNuevo;
    private BigDecimal montoCambio;
    private String motivo;       // "transaccion-debito", "transaccion-credito"
    private Long transaccionId;  // ID de la transacción que causó el cambio
    private String origen;       // "cuenta-service"
}