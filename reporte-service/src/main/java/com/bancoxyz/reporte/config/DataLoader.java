package com.bancoxyz.reporte.config;

import com.bancoxyz.reporte.model.Reporte;
import com.bancoxyz.reporte.repository.ReporteRepository;
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
 * Carga los datos desde cuentas_anuales.csv al iniciar la aplicación.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ReporteRepository reporteRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("📥 Cargando reportes anuales desde CSV...");

        if (reporteRepository.count() > 0) {
            log.info("⏭️ Reportes ya cargados, saltando...");
            return;
        }

        List<Reporte> reportes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/cuentas_anuales.csv").getInputStream()))) {

            String line = reader.readLine(); // Skip header
            int count = 0;

            while ((line = reader.readLine()) != null && count < 30) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        Reporte r = new Reporte();
                        r.setCuentaId(parts[0].trim());
                        r.setFecha(parseFecha(parts[1]));
                        r.setMonto(parseBigDecimal(parts[2]));
                        r.setTipo(parts[3].trim());
                        r.setDescripcion(parts.length > 4 ? parts[4].trim() : "Sin descripción");
                        reportes.add(r);
                        count++;
                    } catch (Exception e) {
                        log.warn("⚠️ Error al procesar reporte: {}", line);
                    }
                }
            }
        }

        reporteRepository.saveAll(reportes);
        log.info("✅ Reportes cargados: {}", reportes.size());
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
