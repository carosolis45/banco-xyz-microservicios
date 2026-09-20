package com.bancoxyz.transaccion.config;

import com.bancoxyz.transaccion.model.Transaccion;
import com.bancoxyz.transaccion.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga los datos desde transacciones.csv al iniciar la aplicación.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final TransaccionRepository transaccionRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("📥 Cargando transacciones desde CSV...");

        if (transaccionRepository.count() > 0) {
            log.info("⏭️ Transacciones ya cargadas, saltando...");
            return;
        }

        List<Transaccion> transacciones = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/transacciones.csv").getInputStream()))) {

            String line = reader.readLine(); // Skip header
            int count = 0;

            while ((line = reader.readLine()) != null && count < 30) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        Transaccion t = new Transaccion();
                        t.setCuentaId(parts[0].trim());
                        t.setFecha(parseFecha(parts[1]));
                        t.setMonto(parseBigDecimal(parts[2]));
                        t.setTipo(parts[3].trim());
                        t.setDescripcion(parts.length > 4 ? parts[4].trim() : "Sin descripción");
                        t.setAnomalia(false);
                        transacciones.add(t);
                        count++;
                    } catch (Exception e) {
                        log.warn("⚠️ Error al procesar transacción: {}", line);
                    }
                }
            }
        }

        transaccionRepository.saveAll(transacciones);
        log.info("✅ Transacciones cargadas: {}", transacciones.size());
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private LocalDate parseFecha(String value) {
        if (value == null || value.trim().isEmpty()) return LocalDate.now();
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            try {
                return LocalDate.parse(value.trim(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            } catch (Exception ex) {
                try {
                    return LocalDate.parse(value.trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                } catch (Exception ex2) {
                    return LocalDate.now();
                }
            }
        }
    }
}
