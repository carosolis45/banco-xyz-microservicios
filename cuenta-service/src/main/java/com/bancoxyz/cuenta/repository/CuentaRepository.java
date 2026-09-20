package com.bancoxyz.cuenta.repository;

import com.bancoxyz.cuenta.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones con Cuenta.
 */
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    Optional<Cuenta> findByCuentaId(String cuentaId);

    List<Cuenta> findByTipo(String tipo);

    List<Cuenta> findByEdadGreaterThanEqual(Integer edad);
}