package com.bancoxyz.clientes.config;

import com.bancoxyz.clientes.model.Cliente;
import com.bancoxyz.clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ClienteRepository clienteRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info(" Cargando clientes desde CSV...");

        if (clienteRepository.count() > 0) {
            log.info("⏭ Clientes ya cargados, saltando...");
            return;
        }

        List<Cliente> clientes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/clientes.csv").getInputStream()))) {

            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    Cliente c = Cliente.builder()
                            .clienteId(parts[0].trim())
                            .nombre(parts[1].trim())
                            .email(parts[2].trim())
                            .telefono(parts[3].trim())
                            .direccion(parts[4].trim())
                            .edad(Integer.parseInt(parts[5].trim()))
                            .tipoCliente(parts[6].trim())
                            .build();
                    clientes.add(c);
                }
            }
        }

        clienteRepository.saveAll(clientes);
        log.info(" Clientes cargados: {}", clientes.size());
    }
}