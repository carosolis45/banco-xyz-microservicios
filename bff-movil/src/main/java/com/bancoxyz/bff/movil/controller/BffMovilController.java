package com.bancoxyz.bff.movil.controller;

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
@RequestMapping("/bff/movil")
@RequiredArgsConstructor
public class BffMovilController {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.clientes.url:http://clientes-service:8084}")
    private String clientesUrl;

    @Value("${services.cuentas.url:http://cuenta-service:8081}")
    private String cuentasUrl;

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("bff", "bff-movil");
        response.put("canal", "MOVIL");
        response.put("descripcion", "BFF para aplicacion movil - respuestas ligeras");
        response.put("version", "1.0.0");
        response.put("estado", "activo");
        response.put("endpoints", new String[]{
                "GET /bff/movil/clientes/resumen",
                "GET /bff/movil/cuentas/saldo"
        });
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clientes/resumen")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> obtenerClientesResumen(
            @RequestHeader("Authorization") String token) {
        log.info(" BFF Móvil: Solicitando clientes (versión ligera)");

        List<?> clientesCompletos = webClientBuilder.build()
                .get()
                .uri(clientesUrl + "/api/clientes")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(List.class)
                .block();

        // Adaptar respuesta: solo datos esenciales para móvil
        List<Map<String, Object>> clientesLigeros = clientesCompletos.stream()
                .map(c -> {
                    Map<String, Object> mapa = (Map<String, Object>) c;
                    Map<String, Object> ligero = new HashMap<>();
                    ligero.put("id", mapa.get("clienteId"));
                    ligero.put("nombre", mapa.get("nombre"));
                    ligero.put("tipo", mapa.get("tipoCliente"));
                    return ligero;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientesLigeros);
    }

    @GetMapping("/cuentas/saldo")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> obtenerSaldos(
            @RequestHeader("Authorization") String token) {
        log.info("📱 BFF Móvil: Solicitando saldos de cuentas (versión ligera)");

        List<?> cuentasCompletas = webClientBuilder.build()
                .get()
                .uri(cuentasUrl + "/api/cuentas")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(List.class)
                .block();

        // Adaptar respuesta: solo saldo esencial
        List<Map<String, Object>> saldos = cuentasCompletas.stream()
                .map(c -> {
                    Map<String, Object> mapa = (Map<String, Object>) c;
                    Map<String, Object> saldo = new HashMap<>();
                    saldo.put("id", mapa.get("cuentaId"));
                    saldo.put("saldo", mapa.get("saldo"));
                    return saldo;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(saldos);
    }
}