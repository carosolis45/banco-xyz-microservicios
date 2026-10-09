package com.bancoxyz.clientes.service;

import com.bancoxyz.clientes.dto.ClienteDTO;
import com.bancoxyz.clientes.model.Cliente;
import com.bancoxyz.clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<ClienteDTO> obtenerTodos() {
        return clienteRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ClienteDTO> obtenerPorClienteId(String clienteId) {
        return clienteRepository.findByClienteId(clienteId)
                .map(this::toDTO);
    }

    public List<ClienteDTO> obtenerPorTipo(String tipo) {
        return clienteRepository.findByTipoCliente(tipo).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ClienteDTO> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO crear(ClienteDTO dto) {
        Cliente cliente = toEntity(dto);
        Cliente guardado = clienteRepository.save(cliente);
        log.info("✅ Cliente creado: {} - {}", guardado.getClienteId(), guardado.getNombre());
        return toDTO(guardado);
    }

    public long contar() {
        return clienteRepository.count();
    }

    private ClienteDTO toDTO(Cliente c) {
        return ClienteDTO.builder()
                .id(c.getId())
                .clienteId(c.getClienteId())
                .nombre(c.getNombre())
                .email(c.getEmail())
                .telefono(c.getTelefono())
                .direccion(c.getDireccion())
                .edad(c.getEdad())
                .tipoCliente(c.getTipoCliente())
                .build();
    }

    private Cliente toEntity(ClienteDTO dto) {
        return Cliente.builder()
                .clienteId(dto.getClienteId())
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .edad(dto.getEdad())
                .tipoCliente(dto.getTipoCliente())
                .build();
    }
}