package com.bancoxyz.batch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaAnual {
    private String cuentaId;
    private String fecha;
    private String transaccion;
    private Double monto;
    private String descripcion;
}