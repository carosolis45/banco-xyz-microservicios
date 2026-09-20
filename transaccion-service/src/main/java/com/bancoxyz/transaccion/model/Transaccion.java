package com.bancoxyz.transaccion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad JPA que representa una transacción bancaria.
 */
@Entity
@Table(name = "transacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuenta_id", nullable = false)
    private String cuentaId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private String tipo;

    @Column
    private String descripcion;

    @Column(nullable = false)
    private Boolean anomalia;
}
