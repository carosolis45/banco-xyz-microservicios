package com.bancoxyz.reporte.consumer;

import com.bancoxyz.reporte.config.JmsConfig;
import com.bancoxyz.reporte.event.CuentaActualizadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de cuentas actualizadas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CuentaEventConsumer {

    @JmsListener(destination = JmsConfig.TOPIC_CUENTA_ACTUALIZADA)
    public void onCuentaActualizada(CuentaActualizadaEvent evento) {
        log.info("[REPORTE] Evento recibido: 'cuenta.saldo.actualizado'");
        log.info("   Cuenta ID: {}", evento.getCuentaId());
        log.info("   Saldo anterior: {}", evento.getSaldoAnterior());
        log.info("   Saldo nuevo: {}", evento.getSaldoNuevo());
        log.info("   Monto cambio: {}", evento.getMontoCambio());
        log.info("   Motivo: {}", evento.getMotivo());
        log.info("   Transacción ID: {}", evento.getTransaccionId());
        log.info("   Origen: {}", evento.getOrigen());
        log.info("Cambio de saldo registrado en el reporte");
    }
}