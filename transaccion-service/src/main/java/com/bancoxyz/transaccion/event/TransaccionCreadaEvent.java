package com.bancoxyz.transaccion.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Evento que se publica cuando se crea una nueva transacción.
 * Se envía por JMS a los microservicios suscritos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionCreadaEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long transaccionId;
    private String cuentaId;
    private LocalDate fecha;
    private BigDecimal monto;
    private String tipo;
    private String descripcion;
    private Boolean anomalia;
    private String origen;  // "transaccion-service"
}