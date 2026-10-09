package com.bancoxyz.batch.processor;

import com.bancoxyz.batch.model.Transaccion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Processor que detecta anomalías en las transacciones.
 * Una transacción es anómala si:
 *  - El monto es menor o igual a 0
 *  - El tipo de transacción no es válido
 */
@Slf4j
@Component
public class TransaccionProcessor implements ItemProcessor<Transaccion, Transaccion> {

    @Override
    public Transaccion process(Transaccion transaccion) throws Exception {
        boolean esAnomalia = false;
        StringBuilder motivo = new StringBuilder();

        // Validación 1: Monto <= 0
        if (transaccion.getMonto() == null || transaccion.getMonto() <= 0) {
            esAnomalia = true;
            motivo.append("Monto inválido (").append(transaccion.getMonto()).append("); ");
        }

        // Validación 2: Tipo no válido
        if (transaccion.getTipo() == null || 
            (!transaccion.getTipo().equalsIgnoreCase("debito") && 
             !transaccion.getTipo().equalsIgnoreCase("credito"))) {
            esAnomalia = true;
            motivo.append("Tipo inválido (").append(transaccion.getTipo()).append("); ");
        }

        transaccion.setAnomalia(esAnomalia);
        transaccion.setMotivoAnomalia(esAnomalia ? motivo.toString().trim() : "OK");

        if (esAnomalia) {
            log.warn(" Anomalía detectada en transacción {}: {}", 
                    transaccion.getId(), transaccion.getMotivoAnomalia());
        }

        return transaccion;
    }
}