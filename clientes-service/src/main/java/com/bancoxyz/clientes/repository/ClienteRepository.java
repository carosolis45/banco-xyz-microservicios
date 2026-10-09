package com.bancoxyz.clientes.repository;

import com.bancoxyz.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByClienteId(String clienteId);

    List<Cliente> findByTipoCliente(String tipoCliente);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
}