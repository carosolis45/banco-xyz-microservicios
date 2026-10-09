package com.bancoxyz.clientes.controller;

import com.bancoxyz.clientes.dto.ClienteDTO;
import com.bancoxyz.clientes.exception.RecursoNoEncontradoException;
import com.bancoxyz.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ClienteDTO>> obtenerTodos() {
        return ResponseEntity.ok(clienteService.obtenerTodos());
    }

    @GetMapping("/{clienteId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable String clienteId) {
        ClienteDTO cliente = clienteService.obtenerPorClienteId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + clienteId));
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ClienteDTO>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(clienteService.obtenerPorTipo(tipo));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ClienteDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(clienteService.buscarPorNombre(nombre));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteDTO> crear(@Valid @RequestBody ClienteDTO dto) {
        ClienteDTO creado = clienteService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("servicio", "clientes-service");
        response.put("version", "1.0.0");
        response.put("estado", "activo");
        response.put("total-clientes", clienteService.contar());
        response.put("endpoints", List.of(
                "GET /api/clientes",
                "GET /api/clientes/{clienteId}",
                "GET /api/clientes/tipo/{tipo}",
                "GET /api/clientes/buscar?nombre=X",
                "POST /api/clientes"
        ));
        return ResponseEntity.ok(response);
    }
}