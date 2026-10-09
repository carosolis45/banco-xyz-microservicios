package com.bancoxyz.batch.processor;

import com.bancoxyz.batch.model.CuentaAnual;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Processor que valida y clasifica movimientos anuales.
 */
@Slf4j
@Component
public class CuentaAnualProcessor implements ItemProcessor<CuentaAnual, CuentaAnual> {

    @Override
    public CuentaAnual process(CuentaAnual cuenta) throws Exception {
        // Clasificar según el signo del monto
        String clasificacion;
        if (cuenta.getMonto() > 0) {
            clasificacion = "INGRESO";
        } else if (cuenta.getMonto() < 0) {
            clasificacion = "EGRESO";
        } else {
            clasificacion = "SIN_MOVIMIENTO";
        }

        log.info("📊 Cuenta {} - {} - {} - {}",
                cuenta.getCuentaId(), cuenta.getFecha(),
                cuenta.getTransaccion(), clasificacion);

        return cuenta;
    }
}