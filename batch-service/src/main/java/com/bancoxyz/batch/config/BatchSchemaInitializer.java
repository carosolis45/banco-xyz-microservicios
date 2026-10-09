package com.bancoxyz.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchSchemaInitializer {

    private final DataSource dataSource;

    @Value("${spring.batch.jdbc.schema:classpath:org/springframework/batch/core/schema-h2.sql}")
    private String schemaScript;

    @Bean
    public DataSourceInitializer batchDataSourceInitializer() {
        log.info("Inicializando schema de Spring Batch desde: {}", schemaScript);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(schemaScript.replace("classpath:", "")));
        populator.setContinueOnError(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}