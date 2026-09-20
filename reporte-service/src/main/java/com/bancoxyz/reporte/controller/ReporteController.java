package com.bancoxyz.reporte.controller;

import com.bancoxyz.reporte.dto.ReporteConCuentaDTO;
import com.bancoxyz.reporte.dto.ReporteDTO;
import com.bancoxyz.reporte.exception.RecursoNoEncontradoException;
import com.bancoxyz.reporte.service.ReporteConCuentaService;
import com.bancoxyz.reporte.service.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el microservicio de reportes.
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final ReporteConCuentaService reporteConCuentaService;

    @GetMapping
    public ResponseEntity<List<ReporteDTO>> obtenerTodos() {
        return ResponseEntity.ok(reporteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReporteDTO> obtenerPorId(@PathVariable Long id) {
        ReporteDTO reporte = reporteService.obtenerPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Reporte no encontrado con ID: " + id));
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<ReporteDTO>> obtenerPorCuenta(@PathVariable String cuentaId) {
        return ResponseEntity.ok(reporteService.obtenerPorCuenta(cuentaId));
    }

    /**
     * Endpoint que combina reportes + datos de la cuenta.
     * Usa Circuit Breaker para llamar al cuenta-service.
     */
    @GetMapping("/cuenta/{cuentaId}/con-detalle")
    public ResponseEntity<ReporteConCuentaDTO> obtenerConDetalle(@PathVariable String cuentaId) {
        return ResponseEntity.ok(reporteConCuentaService.obtenerReportesConCuenta(cuentaId));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ReporteDTO>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(reporteService.obtenerPorTipo(tipo));
    }

    @PostMapping
    public ResponseEntity<ReporteDTO> crear(@Valid @RequestBody ReporteDTO dto) {
        ReporteDTO creado = reporteService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("servicio", "reporte-service");
        response.put("version", "1.0.0");
        response.put("estado", "activo");
        response.put("total-reportes", reporteService.contar());
        response.put("endpoints", List.of(
                "GET /api/reportes",
                "GET /api/reportes/{id}",
                "GET /api/reportes/cuenta/{cuentaId}",
                "GET /api/reportes/cuenta/{cuentaId}/con-detalle",
                "GET /api/reportes/tipo/{tipo}",
                "POST /api/reportes"
        ));
        return ResponseEntity.ok(response);
    }
}
