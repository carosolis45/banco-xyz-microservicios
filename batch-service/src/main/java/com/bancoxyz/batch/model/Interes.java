package com.bancoxyz.batch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Interes {
    private String cuentaId;
    private String nombre;
    private Double saldo;
    private Integer edad;
    private String tipo;
    private Double interesCalculado;
    private Double saldoFinal;
}
