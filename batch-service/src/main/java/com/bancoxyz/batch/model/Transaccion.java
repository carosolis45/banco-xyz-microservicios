package com.bancoxyz.batch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {
    private Long id;
    private String fecha;
    private Double monto;
    private String tipo;
    private boolean anomalia;
    private String motivoAnomalia;
}