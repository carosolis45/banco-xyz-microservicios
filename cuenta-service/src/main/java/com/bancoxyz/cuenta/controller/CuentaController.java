package com.bancoxyz.cuenta.controller;

import com.bancoxyz.cuenta.dto.CuentaDTO;
import com.bancoxyz.cuenta.exception.RecursoNoEncontradoException;
import com.bancoxyz.cuenta.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el microservicio de cuentas.
 * Los endpoints están protegidos con OAuth2 (Keycloak).
 */
@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    /**
     * Lista todas las cuentas. Requiere rol USER o ADMIN.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CuentaDTO>> obtenerTodas() {
        return ResponseEntity.ok(cuentaService.obtenerTodas());
    }

    /**
     * Obtiene una cuenta por su cuentaId. Requiere rol USER o ADMIN.
     */
    @GetMapping("/{cuentaId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CuentaDTO> obtenerPorId(@PathVariable String cuentaId) {
        CuentaDTO cuenta = cuentaService.obtenerPorCuentaId(cuentaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cuenta no encontrada con ID: " + cuentaId));
        return ResponseEntity.ok(cuenta);
    }

    /**
     * Lista cuentas por tipo. Requiere rol USER o ADMIN.
     */
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CuentaDTO>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(cuentaService.obtenerPorTipo(tipo));
    }

    /**
     * Crea una nueva cuenta. Solo ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CuentaDTO> crear(@Valid @RequestBody CuentaDTO dto) {
        CuentaDTO creada = cuentaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * Endpoint de bienvenida. Público (sin autenticación).
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("servicio", "cuenta-service");
        response.put("version", "1.0.0");
        response.put("estado", "activo");
        response.put("total-cuentas", cuentaService.contar());
        response.put("endpoints", List.of(
                "GET /api/cuentas",
                "GET /api/cuentas/{cuentaId}",
                "GET /api/cuentas/tipo/{tipo}",
                "POST /api/cuentas"
        ));
        return ResponseEntity.ok(response);
    }
}