package com.bancoxyz.reporte.consumer;

import com.bancoxyz.reporte.config.JmsConfig;
import com.bancoxyz.reporte.event.TransaccionCreadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de anomalías.
 * Solo este servicio escucha la Queue de anomalías.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnomaliaEventConsumer {

    @JmsListener(destination = JmsConfig.QUEUE_TRANSACCION_ANOMALIA)
    public void onTransaccionAnomalia(TransaccionCreadaEvent evento) {
        log.warn(" [REPORTE] Evento recibido: 'transaccion.anomalia'");
        log.warn("   Transacción ID: {}", evento.getTransaccionId());
        log.warn("   Cuenta ID: {}", evento.getCuentaId());
        log.warn("   Monto: {}", evento.getMonto());
        log.warn("   Descripción: {}", evento.getDescripcion());
        log.warn("   Requiere revisión manual");
        log.warn(" Anomalía registrada para revisión");
    }
}