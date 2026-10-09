package com.bancoxyz.bff.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/bff/web")
@RequiredArgsConstructor
public class BffWebController {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.clientes.url:http://clientes-service:8084}")
    private String clientesUrl;

    @Value("${services.cuentas.url:http://cuenta-service:8081}")
    private String cuentasUrl;

    @Value("${services.pagos.url:http://transaccion-service:8082}")
    private String pagosUrl;

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("bff", "bff-web");
        response.put("canal", "WEB");
        response.put("descripcion", "Backend for Frontend para navegadores - datos completos");
        response.put("version", "1.0.0");
        response.put("estado", "activo");
        response.put("endpoints", new String[]{
                "GET /bff/web/clientes",
                "GET /bff/web/cuentas",
                "GET /bff/web/pagos",
                "GET /bff/web/dashboard"
        });
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clientes")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> obtenerClientes(@RequestHeader("Authorization") String token) {
        log.info("🌐 BFF Web: Solicitando clientes");
        Object response = webClientBuilder.build()
                .get()
                .uri(clientesUrl + "/api/clientes")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cuentas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> obtenerCuentas(@RequestHeader("Authorization") String token) {
        log.info("🌐 BFF Web: Solicitando cuentas");
        Object response = webClientBuilder.build()
                .get()
                .uri(cuentasUrl + "/api/cuentas")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pagos")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> obtenerPagos(@RequestHeader("Authorization") String token) {
        log.info("🌐 BFF Web: Solicitando pagos");
        Object response = webClientBuilder.build()
                .get()
                .uri(pagosUrl + "/api/transacciones")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> dashboard(@RequestHeader("Authorization") String token) {
        log.info("🌐 BFF Web: Construyendo dashboard completo");

        Object clientes = webClientBuilder.build().get()
                .uri(clientesUrl + "/api/clientes")
                .header("Authorization", token).retrieve().bodyToMono(Object.class).block();

        Object cuentas = webClientBuilder.build().get()
                .uri(cuentasUrl + "/api/cuentas")
                .header("Authorization", token).retrieve().bodyToMono(Object.class).block();

        Object pagos = webClientBuilder.build().get()
                .uri(pagosUrl + "/api/transacciones")
                .header("Authorization", token).retrieve().bodyToMono(Object.class).block();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("canal", "WEB");
        dashboard.put("clientes", clientes);
        dashboard.put("cuentas", cuentas);
        dashboard.put("pagos", pagos);
        dashboard.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(dashboard);
    }
}