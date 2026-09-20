package com.bancoxyz.reporte.repository;

import com.bancoxyz.reporte.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para operaciones con Reporte.
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByCuentaId(String cuentaId);

    List<Reporte> findByTipo(String tipo);
}
