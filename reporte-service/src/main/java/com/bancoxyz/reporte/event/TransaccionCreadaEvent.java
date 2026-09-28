package com.bancoxyz.reporte.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

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
    private String origen;
}