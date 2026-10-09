package com.bancoxyz.batch.processor;

import com.bancoxyz.batch.model.Interes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Processor que calcula los intereses mensuales según el tipo de cuenta:
 *  - ahorro: 5% anual
 *  - prestamo: 10% anual
 *  - hipoteca: 7% anual
 */
@Slf4j
@Component
public class InteresProcessor implements ItemProcessor<Interes, Interes> {

    private static final double TASA_AHORRO = 0.05;
    private static final double TASA_PRESTAMO = 0.10;
    private static final double TASA_HIPOTECA = 0.07;

    @Override
    public Interes process(Interes interes) throws Exception {
        double tasa = obtenerTasa(interes.getTipo());
        double interesCalculado = interes.getSaldo() * tasa;
        double saldoFinal = interes.getSaldo() + interesCalculado;

        interes.setInteresCalculado(interesCalculado);
        interes.setSaldoFinal(saldoFinal);

        log.info(" Cuenta {} - Tipo: {} - Saldo: {} - Interés: {} - Saldo final: {}",
                interes.getCuentaId(), interes.getTipo(),
                interes.getSaldo(), interesCalculado, saldoFinal);

        return interes;
    }

    private double obtenerTasa(String tipo) {
        if (tipo == null) return 0;
        return switch (tipo.toLowerCase()) {
            case "ahorro" -> TASA_AHORRO;
            case "prestamo" -> TASA_PRESTAMO;
            case "hipoteca" -> TASA_HIPOTECA;
            default -> 0;
        };
    }
}