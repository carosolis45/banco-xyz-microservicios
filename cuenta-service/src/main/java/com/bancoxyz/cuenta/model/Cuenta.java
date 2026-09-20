package com.bancoxyz.cuenta.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad JPA que representa una cuenta bancaria.
 * Se carga desde el archivo intereses.csv al iniciar la aplicación.
 */
@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuenta_id", unique = true, nullable = false)
    private String cuentaId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false)
    private String tipo;
}