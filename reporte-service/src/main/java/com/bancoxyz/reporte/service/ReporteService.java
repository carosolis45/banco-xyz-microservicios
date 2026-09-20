package com.bancoxyz.reporte.service;

import com.bancoxyz.reporte.dto.ReporteDTO;
import com.bancoxyz.reporte.model.Reporte;
import com.bancoxyz.reporte.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio con la lógica de negocio de reportes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public List<ReporteDTO> obtenerTodos() {
        log.debug("Obteniendo todos los reportes");
        return reporteRepository.findAll()
                .stream()
                .map(ReporteDTO::new)
                .toList();
    }

    public Optional<ReporteDTO> obtenerPorId(Long id) {
        log.debug("Buscando reporte: {}", id);
        return reporteRepository.findById(id)
                .map(ReporteDTO::new);
    }

    public List<ReporteDTO> obtenerPorCuenta(String cuentaId) {
        log.debug("Buscando reportes de la cuenta: {}", cuentaId);
        return reporteRepository.findByCuentaId(cuentaId)
                .stream()
                .map(ReporteDTO::new)
                .toList();
    }

    public List<ReporteDTO> obtenerPorTipo(String tipo) {
        log.debug("Buscando reportes por tipo: {}", tipo);
        return reporteRepository.findByTipo(tipo)
                .stream()
                .map(ReporteDTO::new)
                .toList();
    }

    public ReporteDTO crear(ReporteDTO dto) {
        log.info("Creando nuevo reporte para cuenta: {}", dto.getCuentaId());
        Reporte reporte = dto.toEntity();
        Reporte guardado = reporteRepository.save(reporte);
        return new ReporteDTO(guardado);
    }

    public long contar() {
        return reporteRepository.count();
    }
}
