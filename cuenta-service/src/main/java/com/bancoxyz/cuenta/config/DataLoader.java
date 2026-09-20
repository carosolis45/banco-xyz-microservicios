package com.bancoxyz.cuenta.config;

import com.bancoxyz.cuenta.model.Cuenta;
import com.bancoxyz.cuenta.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga los datos desde intereses.csv al iniciar la aplicación.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CuentaRepository cuentaRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Cargando datos de cuentas desde CSV...");

        if (cuentaRepository.count() > 0) {
            log.info("⏭Cuentas ya cargadas, saltando...");
            return;
        }

        List<Cuenta> cuentas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/intereses.csv").getInputStream()))) {

            String line = reader.readLine(); // Skip header
            int count = 0;

            while ((line = reader.readLine()) != null && count < 20) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    try {
                        Cuenta cuenta = new Cuenta();
                        cuenta.setCuentaId(parts[0].trim());
                        cuenta.setNombre(parts[1].trim());
                        cuenta.setSaldo(parseBigDecimal(parts[2]));
                        cuenta.setEdad(parseInteger(parts[3]));
                        cuenta.setTipo(parts[4].trim());
                        cuentas.add(cuenta);
                        count++;
                    } catch (Exception e) {
                        log.warn("Error al procesar cuenta: {}", line);
                    }
                }
            }
        }

        cuentaRepository.saveAll(cuentas);
        log.info("Cuentas cargadas: {}", cuentas.size());
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}