package com.bancoxyz.cuenta.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración JMS para ActiveMQ.
 */
@Configuration
public class JmsConfig {

    public static final String TOPIC_TRANSACCION_CREADA = "transaccion.creada";
    public static final String TOPIC_CUENTA_ACTUALIZADA = "cuenta.saldo.actualizado";

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        // Mapear los tipos de otros microservicios a las clases locales
        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put(
                "com.bancoxyz.transaccion.event.TransaccionCreadaEvent",
                com.bancoxyz.cuenta.event.TransaccionCreadaEvent.class
        );
        converter.setTypeIdMappings(typeIdMappings);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        converter.setObjectMapper(objectMapper);

        return converter;
    }
}