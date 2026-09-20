package com.bancoxyz.reporte.service;

import com.bancoxyz.reporte.dto.ReporteConCuentaDTO;
import com.bancoxyz.reporte.dto.ReporteDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Servicio que combina reportes con datos de la cuenta.
 * Usa Circuit Breaker para llamar al cuenta-service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteConCuentaService {

    private final ReporteService reporteService;
    private final RestTemplate restTemplate;

    private static final String CUENTA_SERVICE_URL = "http://cuenta-service/api/cuentas/";

    /**
     * Obtiene reportes + datos de la cuenta usando Circuit Breaker.
     */
    @CircuitBreaker(name = "cuentaService", fallbackMethod = "fallbackObtenerReportesConCuenta")
    public ReporteConCuentaDTO obtenerReportesConCuenta(String cuentaId) {
        log.info("🔍 Consultando reportes y cuenta para: {}", cuentaId);

        List<ReporteDTO> reportes = reporteService.obtenerPorCuenta(cuentaId);

        @SuppressWarnings("unchecked")
        Map<String, Object> cuenta = restTemplate.getForObject(
                CUENTA_SERVICE_URL + cuentaId,
                Map.class
        );

        log.info("✅ Datos obtenidos correctamente para cuenta: {}", cuentaId);

        ReporteConCuentaDTO response = new ReporteConCuentaDTO();
        response.setCuentaId(cuentaId);
        response.setReportes(reportes);
        response.setCuenta(cuenta);
        response.setFuente("real");

        return response;
    }

    /**
     * Fallback cuando el Circuit Breaker está abierto o hay error.
     */
    public ReporteConCuentaDTO fallbackObtenerReportesConCuenta(String cuentaId, Throwable t) {
        log.warn("⚠️ Fallback activado para cuenta: {}. Error: {}", cuentaId, t.getMessage());

        List<ReporteDTO> reportes = reporteService.obtenerPorCuenta(cuentaId);

        ReporteConCuentaDTO response = new ReporteConCuentaDTO();
        response.setCuentaId(cuentaId);
        response.setReportes(reportes);
        response.setCuenta(null);
        response.setFuente("fallback");

        return response;
    }
}