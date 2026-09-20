package com.bancoxyz.transaccion.repository;

import com.bancoxyz.transaccion.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para operaciones con Transaccion.
 */
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByCuentaId(String cuentaId);

    List<Transaccion> findByTipo(String tipo);

    List<Transaccion> findByAnomaliaTrue();
}
