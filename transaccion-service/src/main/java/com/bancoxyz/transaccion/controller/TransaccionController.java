package com.bancoxyz.transaccion.controller;

import com.bancoxyz.transaccion.dto.TransaccionDTO;
import com.bancoxyz.transaccion.exception.RecursoNoEncontradoException;
import com.bancoxyz.transaccion.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el microservicio de transacciones.
 */
@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;

    @GetMapping
    public ResponseEntity<List<TransaccionDTO>> obtenerTodas() {
        return ResponseEntity.ok(transaccionService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionDTO> obtenerPorId(@PathVariable Long id) {
        TransaccionDTO transaccion = transaccionService.obtenerPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Transacción no encontrada con ID: " + id));
        return ResponseEntity.ok(transaccion);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<TransaccionDTO>> obtenerPorCuenta(@PathVariable String cuentaId) {
        return ResponseEntity.ok(transaccionService.obtenerPorCuenta(cuentaId));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<TransaccionDTO>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(transaccionService.obtenerPorTipo(tipo));
    }

    @GetMapping("/anomalias")
    public ResponseEntity<List<TransaccionDTO>> obtenerAnomalias() {
        return ResponseEntity.ok(transaccionService.obtenerAnomalias());
    }

    @PostMapping
    public ResponseEntity<TransaccionDTO> crear(@Valid @RequestBody TransaccionDTO dto) {
        TransaccionDTO creada = transaccionService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("servicio", "transaccion-service");
        response.put("version", "1.0.0");
        response.put("estado", "activo");
        response.put("total-transacciones", transaccionService.contar());
        response.put("endpoints", List.of(
                "GET /api/transacciones",
                "GET /api/transacciones/{id}",
                "GET /api/transacciones/cuenta/{cuentaId}",
                "GET /api/transacciones/tipo/{tipo}",
                "GET /api/transacciones/anomalias",
                "POST /api/transacciones"
        ));
        return ResponseEntity.ok(response);
    }
}
