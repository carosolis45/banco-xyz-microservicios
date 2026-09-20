package com.bancoxyz.reporte.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO que combina reportes + datos de la cuenta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteConCuentaDTO {

    private String cuentaId;
    private List<ReporteDTO> reportes;
    private Map<String, Object> cuenta;
    private String fuente;  // "real" o "fallback"
}