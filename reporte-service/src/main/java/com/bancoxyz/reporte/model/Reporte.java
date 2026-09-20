package com.bancoxyz.reporte.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad JPA que representa un reporte anual de cuenta.
 */
@Entity
@Table(name = "reportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

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
}
