package com.bancoxyz.cuenta.producer;

import com.bancoxyz.cuenta.config.JmsConfig;
import com.bancoxyz.cuenta.event.CuentaActualizadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Productor de eventos de cuentas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CuentaEventProducer {

    private final JmsTemplate jmsTemplate;

    /**
     * Publica un evento en el Topic "cuenta.saldo.actualizado".
     */
    public void publicarCuentaActualizada(CuentaActualizadaEvent evento) {
        log.info("Publicando evento 'cuenta.saldo.actualizado': {}", evento);
        jmsTemplate.convertAndSend(JmsConfig.TOPIC_CUENTA_ACTUALIZADA, evento);
        log.info("Evento publicado exitosamente");
    }
}