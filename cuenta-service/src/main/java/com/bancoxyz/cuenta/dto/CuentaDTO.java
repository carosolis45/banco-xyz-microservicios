package com.bancoxyz.cuenta.dto;

import com.bancoxyz.cuenta.model.Cuenta;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para transferir datos de Cuenta entre capas.
 * Incluye validaciones para entrada de datos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {

    private Long id;

    @NotBlank(message = "El ID de cuenta es obligatorio")
    @Size(min = 1, max = 20, message = "El ID debe tener entre 1 y 20 caracteres")
    private String cuentaId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotNull(message = "El saldo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo no puede ser negativo")
    private BigDecimal saldo;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "La edad mínima es 18 años")
    @Max(value = 120, message = "La edad máxima es 120 años")
    private Integer edad;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "ahorro|prestamo|hipoteca", 
             message = "El tipo debe ser: ahorro, prestamo o hipoteca")
    private String tipo;

    public CuentaDTO(Cuenta cuenta) {
        this.id = cuenta.getId();
        this.cuentaId = cuenta.getCuentaId();
        this.nombre = cuenta.getNombre();
        this.saldo = cuenta.getSaldo();
        this.edad = cuenta.getEdad();
        this.tipo = cuenta.getTipo();
    }

    public Cuenta toEntity() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(this.id);
        cuenta.setCuentaId(this.cuentaId);
        cuenta.setNombre(this.nombre);
        cuenta.setSaldo(this.saldo);
        cuenta.setEdad(this.edad);
        cuenta.setTipo(this.tipo);
        return cuenta;
    }
}