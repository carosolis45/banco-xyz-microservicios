package com.bancoxyz.bff.cajeros.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/bff/cajeros")
@RequiredArgsConstructor
public class BffCajerosController {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.cuentas.url:http://cuenta-service:8081}")
    private String cuentasUrl;

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("bff", "bff-cajeros");
        response.put("canal", "CAJEROS_AUTOMATICOS");
        response.put("descripcion", "BFF para cajeros - operaciones criticas");
        response.put("version", "1.0.0");
        response.put("estado", "activo");
        response.put("nivel-seguridad", "ALTO");
        response.put("endpoints", new String[]{
                "GET /bff/cajeros/cuenta/{cuentaId}/saldo",
                "POST /bff/cajeros/cuenta/{cuentaId}/retiro",
                "GET /bff/cajeros/cuenta/{cuentaId}/movimientos"
        });
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cuenta/{cuentaId}/saldo")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> consultarSaldo(
            @PathVariable String cuentaId,
            @RequestHeader("Authorization") String token) {
        log.info(" BFF Cajeros: Consultando saldo de cuenta {}", cuentaId);

        Object cuenta = webClientBuilder.build()
                .get()
                .uri(cuentasUrl + "/api/cuentas/" + cuentaId)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        Map<String, Object> response = new HashMap<>();
        response.put("operacion", "CONSULTA_SALDO");
        response.put("cuenta", cuenta);
        response.put("timestamp", System.currentTimeMillis());
        response.put("exitoso", true);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cuenta/{cuentaId}/retiro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> realizarRetiro(
            @PathVariable String cuentaId,
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        log.info(" BFF Cajeros: Retiro en cuenta {} por monto {}", cuentaId, request.get("monto"));

        //  Validaciones de seguridad adicionales para cajeros
        Object montoObj = request.get("monto");
        if (montoObj == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("operacion", "RETIRO");
            error.put("exitoso", false);
            error.put("mensaje", "El monto es obligatorio");
            return ResponseEntity.badRequest().body(error);
        }

        double monto = Double.parseDouble(montoObj.toString());
        if (monto <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("operacion", "RETIRO");
            error.put("exitoso", false);
            error.put("mensaje", "El monto debe ser mayor a 0");
            return ResponseEntity.badRequest().body(error);
        }

        if (monto > 1000000) {
            Map<String, Object> error = new HashMap<>();
            error.put("operacion", "RETIRO");
            error.put("exitoso", false);
            error.put("mensaje", "El monto excede el limite permitido por operacion");
            return ResponseEntity.badRequest().body(error);
        }

        // Aquí llamaríamos al servicio de transacciones para procesar el retiro
        Map<String, Object> response = new HashMap<>();
        response.put("operacion", "RETIRO");
        response.put("cuenta", cuentaId);
        response.put("monto", monto);
        response.put("timestamp", System.currentTimeMillis());
        response.put("exitoso", true);
        response.put("mensaje", "Retiro procesado (simulado)");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cuenta/{cuentaId}/movimientos")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<?>> consultarMovimientos(
            @PathVariable String cuentaId,
            @RequestHeader("Authorization") String token) {
        log.info(" BFF Cajeros: Consultando movimientos de cuenta {}", cuentaId);

        List<?> movimientos = webClientBuilder.build()
                .get()
                .uri(cuentasUrl + "/api/transacciones/cuenta/" + cuentaId)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(List.class)
                .block();

        return ResponseEntity.ok(movimientos);
    }
}