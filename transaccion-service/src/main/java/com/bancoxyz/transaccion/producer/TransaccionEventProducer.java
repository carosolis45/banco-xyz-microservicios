package com.bancoxyz.transaccion.producer;

import com.bancoxyz.transaccion.config.JmsConfig;
import com.bancoxyz.transaccion.event.TransaccionCreadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Productor de eventos de transacciones.
 * Publica mensajes en ActiveMQ (Topics y Queues).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransaccionEventProducer {

    private final JmsTemplate jmsTemplate;

    /**
     * Publica un evento en el Topic "transaccion.creada".
     * Múltiples servicios lo consumen (cuenta-service, reporte-service).
     */
    public void publicarTransaccionCreada(TransaccionCreadaEvent evento) {
        log.info("Publicando evento 'transaccion.creada': {}", evento);
        // Asegurar que enviamos a un Topic
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend(JmsConfig.TOPIC_TRANSACCION_CREADA, evento);
        log.info("Evento publicado exitosamente");
    }

    /**
     * Publica un evento en la Queue "transaccion.anomalia".
     * Solo un consumidor lo procesa (reporte-service).
     */
    public void publicarTransaccionAnomalia(TransaccionCreadaEvent evento) {
        log.warn("Publicando evento 'transaccion.anomalia': {}", evento);
        // Desactivar pub-sub para enviar a Queue
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.convertAndSend(JmsConfig.QUEUE_TRANSACCION_ANOMALIA, evento);
        log.warn("Evento de anomalía publicado");
    }
}