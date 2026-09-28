package com.bancoxyz.reporte.consumer;

import com.bancoxyz.reporte.config.JmsConfig;
import com.bancoxyz.reporte.event.TransaccionCreadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de transacciones creadas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransaccionEventConsumer {

    @JmsListener(destination = JmsConfig.TOPIC_TRANSACCION_CREADA)
    public void onTransaccionCreada(TransaccionCreadaEvent evento) {
        log.info(" [REPORTE] Evento recibido: 'transaccion.creada'");
        log.info("   Transacción ID: {}", evento.getTransaccionId());
        log.info("   Cuenta ID: {}", evento.getCuentaId());
        log.info("   Monto: {}", evento.getMonto());
        log.info("   Tipo: {}", evento.getTipo());
        log.info("   Fecha: {}", evento.getFecha());
        log.info("   Anomalía: {}", evento.getAnomalia());
        log.info("   Origen: {}", evento.getOrigen());
        log.info("  Evento registrado en el reporte");
    }
}