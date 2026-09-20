package com.bancoxyz.cuenta.service;

import com.bancoxyz.cuenta.dto.CuentaDTO;
import com.bancoxyz.cuenta.model.Cuenta;
import com.bancoxyz.cuenta.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio con la lógica de negocio de cuentas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public List<CuentaDTO> obtenerTodas() {
        log.debug("Obteniendo todas las cuentas");
        return cuentaRepository.findAll()
                .stream()
                .map(CuentaDTO::new)
                .toList();
    }

    public Optional<CuentaDTO> obtenerPorCuentaId(String cuentaId) {
        log.debug("Buscando cuenta: {}", cuentaId);
        return cuentaRepository.findByCuentaId(cuentaId)
                .map(CuentaDTO::new);
    }

    public List<CuentaDTO> obtenerPorTipo(String tipo) {
        log.debug("Buscando cuentas por tipo: {}", tipo);
        return cuentaRepository.findByTipo(tipo)
                .stream()
                .map(CuentaDTO::new)
                .toList();
    }

    public CuentaDTO crear(CuentaDTO dto) {
        log.info("Creando nueva cuenta: {}", dto.getCuentaId());
        Cuenta cuenta = dto.toEntity();
        Cuenta guardada = cuentaRepository.save(cuenta);
        return new CuentaDTO(guardada);
    }

    public long contar() {
        return cuentaRepository.count();
    }
}