package com.bancoxyz.cuenta.consumer;

import com.bancoxyz.cuenta.config.JmsConfig;
import com.bancoxyz.cuenta.event.CuentaActualizadaEvent;
import com.bancoxyz.cuenta.event.TransaccionCreadaEvent;
import com.bancoxyz.cuenta.model.Cuenta;
import com.bancoxyz.cuenta.producer.CuentaEventProducer;
import com.bancoxyz.cuenta.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Consumidor de eventos de transacciones.
 * Actualiza el saldo de la cuenta cuando se recibe un evento.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransaccionEventConsumer {

    private final CuentaRepository cuentaRepository;
    private final CuentaEventProducer cuentaEventProducer;

    /**
     * Escucha eventos del Topic "transaccion.creada".
     */
    @JmsListener(destination = JmsConfig.TOPIC_TRANSACCION_CREADA)
    public void onTransaccionCreada(TransaccionCreadaEvent evento) {
        log.info("Evento recibido: transaccion.creada");
        log.info("   Transacción ID: {}", evento.getTransaccionId());
        log.info("   Cuenta ID: {}", evento.getCuentaId());
        log.info("   Monto: {}", evento.getMonto());
        log.info("   Tipo: {}", evento.getTipo());

        // Buscar la cuenta
        Optional<Cuenta> cuentaOpt = cuentaRepository.findByCuentaId(evento.getCuentaId());

        if (cuentaOpt.isEmpty()) {
            log.error("Cuenta no encontrada: {}", evento.getCuentaId());
            return;
        }

        Cuenta cuenta = cuentaOpt.get();
        BigDecimal saldoAnterior = cuenta.getSaldo();
        BigDecimal montoCambio = evento.getMonto();
        BigDecimal saldoNuevo;

        // Aplicar el cambio según el tipo de transacción
        if ("credito".equalsIgnoreCase(evento.getTipo())) {
            saldoNuevo = saldoAnterior.add(montoCambio);
        } else if ("debito".equalsIgnoreCase(evento.getTipo())) {
            saldoNuevo = saldoAnterior.subtract(montoCambio);
        } else {
            log.warn("Tipo de transacción desconocido: {}", evento.getTipo());
            saldoNuevo = saldoAnterior;
        }

        // Actualizar saldo
        cuenta.setSaldo(saldoNuevo);
        cuentaRepository.save(cuenta);

        log.info("✅ Saldo actualizado:");
        log.info("   Cuenta: {}", cuenta.getCuentaId());
        log.info("   Saldo anterior: {}", saldoAnterior);
        log.info("   Saldo nuevo: {}", saldoNuevo);

        // Publicar evento de cuenta actualizada
        CuentaActualizadaEvent cuentaEvent = new CuentaActualizadaEvent(
                cuenta.getCuentaId(),
                saldoAnterior,
                saldoNuevo,
                montoCambio,
                "transaccion-" + evento.getTipo(),
                evento.getTransaccionId(),
                "cuenta-service"
        );

        cuentaEventProducer.publicarCuentaActualizada(cuentaEvent);
    }
}