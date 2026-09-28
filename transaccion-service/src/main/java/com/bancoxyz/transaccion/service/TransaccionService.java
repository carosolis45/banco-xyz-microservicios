package com.bancoxyz.transaccion.service;

import com.bancoxyz.transaccion.dto.TransaccionDTO;
import com.bancoxyz.transaccion.event.TransaccionCreadaEvent;
import com.bancoxyz.transaccion.model.Transaccion;
import com.bancoxyz.transaccion.producer.TransaccionEventProducer;
import com.bancoxyz.transaccion.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio con la lógica de negocio de transacciones.
 * Publica eventos JMS cuando se crean transacciones.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final TransaccionEventProducer transaccionEventProducer;

    public List<TransaccionDTO> obtenerTodas() {
        log.debug("Obteniendo todas las transacciones");
        return transaccionRepository.findAll()
                .stream()
                .map(TransaccionDTO::new)
                .toList();
    }

    public Optional<TransaccionDTO> obtenerPorId(Long id) {
        log.debug("Buscando transacción: {}", id);
        return transaccionRepository.findById(id)
                .map(TransaccionDTO::new);
    }

    public List<TransaccionDTO> obtenerPorCuenta(String cuentaId) {
        log.debug("Buscando transacciones de la cuenta: {}", cuentaId);
        return transaccionRepository.findByCuentaId(cuentaId)
                .stream()
                .map(TransaccionDTO::new)
                .toList();
    }

    public List<TransaccionDTO> obtenerPorTipo(String tipo) {
        log.debug("Buscando transacciones por tipo: {}", tipo);
        return transaccionRepository.findByTipo(tipo)
                .stream()
                .map(TransaccionDTO::new)
                .toList();
    }

    public List<TransaccionDTO> obtenerAnomalias() {
        log.debug("Buscando transacciones anómalas");
        return transaccionRepository.findByAnomaliaTrue()
                .stream()
                .map(TransaccionDTO::new)
                .toList();
    }

    /**
     * Crea una nueva transacción y publica eventos JMS.
     */
    public TransaccionDTO crear(TransaccionDTO dto) {
        log.info("Creando nueva transacción para cuenta: {}", dto.getCuentaId());

        Transaccion transaccion = dto.toEntity();
        Transaccion guardada = transaccionRepository.save(transaccion);
        log.info("Transacción guardada con ID: {}", guardada.getId());

        // Crear evento
        TransaccionCreadaEvent evento = new TransaccionCreadaEvent(
                guardada.getId(),
                guardada.getCuentaId(),
                guardada.getFecha(),
                guardada.getMonto(),
                guardada.getTipo(),
                guardada.getDescripcion(),
                guardada.getAnomalia(),
                "transaccion-service"
        );

        // Publicar en Topic "transaccion.creada"
        transaccionEventProducer.publicarTransaccionCreada(evento);

        // Si hay anomalía, publicar en Queue "transaccion.anomalia"
        if (Boolean.TRUE.equals(guardada.getAnomalia())) {
            transaccionEventProducer.publicarTransaccionAnomalia(evento);
        }

        return new TransaccionDTO(guardada);
    }

    public long contar() {
        return transaccionRepository.count();
    }
}
