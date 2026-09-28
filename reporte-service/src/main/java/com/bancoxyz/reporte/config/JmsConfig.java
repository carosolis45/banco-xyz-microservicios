package com.bancoxyz.reporte.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JmsConfig {

    public static final String TOPIC_TRANSACCION_CREADA = "transaccion.creada";
    public static final String TOPIC_CUENTA_ACTUALIZADA = "cuenta.saldo.actualizado";
    public static final String QUEUE_TRANSACCION_ANOMALIA = "transaccion.anomalia";

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put(
                "com.bancoxyz.transaccion.event.TransaccionCreadaEvent",
                com.bancoxyz.reporte.event.TransaccionCreadaEvent.class
        );
        typeIdMappings.put(
                "com.bancoxyz.cuenta.event.CuentaActualizadaEvent",
                com.bancoxyz.reporte.event.CuentaActualizadaEvent.class
        );
        converter.setTypeIdMappings(typeIdMappings);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        converter.setObjectMapper(objectMapper);

        return converter;
    }
}