package com.bancoxyz.cuenta.controller;

import com.bancoxyz.cuenta.dto.LoginRequestDTO;
import com.bancoxyz.cuenta.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de autenticación con JWT.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String rol = auth.getAuthorities().iterator().next().getAuthority();
            String token = jwtUtil.generarToken(auth.getName(), rol);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", auth.getName());
            response.put("rol", rol);
            response.put("mensaje", "Autenticación exitosa");

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(
                    Map.of("error", "Usuario o contraseña incorrectos")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("error", "Error interno: " + e.getMessage())
            );
        }
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("servicio", "cuenta-service");
        response.put("endpoint-login", "POST /api/auth/login");
        response.put("usuarios", Map.of(
                "admin", "admin123 (roles: ADMIN, USER)",
                "user", "user123 (rol: USER)"
        ));
        return ResponseEntity.ok(response);
    }
}