package com.bancoxyz.batch.config;

import com.bancoxyz.batch.listener.JobCompletionListener;
import com.bancoxyz.batch.model.CuentaAnual;
import com.bancoxyz.batch.processor.CuentaAnualProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EstadosCuentaAnualesJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CuentaAnualProcessor cuentaAnualProcessor;
    private final JobCompletionListener jobCompletionListener;

    @Bean
    public Job estadosCuentaAnualesJob() {
        return new JobBuilder("estadosCuentaAnualesJob", jobRepository)
                .listener(jobCompletionListener)
                .start(estadosCuentaAnualesStep())
                .build();
    }

    @Bean
    public Step estadosCuentaAnualesStep() {
        return new StepBuilder("estadosCuentaAnualesStep", jobRepository)
                .<CuentaAnual, CuentaAnual>chunk(5, transactionManager)
                .reader(cuentaAnualReader())
                .processor(cuentaAnualProcessor)
                .writer(cuentaAnualWriter())
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean
    public ItemReader<CuentaAnual> cuentaAnualReader() {
        return new FlatFileItemReaderBuilder<CuentaAnual>()
                .name("cuentaAnualReader")
                .resource(new ClassPathResource("data/cuentas_anuales.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("cuentaId", "fecha", "transaccion", "monto", "descripcion")
                .targetType(CuentaAnual.class)
                .build();
    }

    @Bean
    public ItemWriter<CuentaAnual> cuentaAnualWriter() {
        return items -> {
            log.info("📊 Procesados {} movimientos anuales", items.size());
            for (CuentaAnual c : items) {
                log.info("   Cuenta {} - {} - {} - {}",
                        c.getCuentaId(), c.getFecha(), c.getTransaccion(), c.getMonto());
            }
        };
    }
}