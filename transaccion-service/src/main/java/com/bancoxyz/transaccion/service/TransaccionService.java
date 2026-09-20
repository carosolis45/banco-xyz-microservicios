package com.bancoxyz.transaccion.service;

import com.bancoxyz.transaccion.dto.TransaccionDTO;
import com.bancoxyz.transaccion.model.Transaccion;
import com.bancoxyz.transaccion.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio con la lógica de negocio de transacciones.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

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

    public TransaccionDTO crear(TransaccionDTO dto) {
        log.info("Creando nueva transacción para cuenta: {}", dto.getCuentaId());
        Transaccion transaccion = dto.toEntity();
        Transaccion guardada = transaccionRepository.save(transaccion);
        return new TransaccionDTO(guardada);
    }

    public long contar() {
        return transaccionRepository.count();
    }
}
