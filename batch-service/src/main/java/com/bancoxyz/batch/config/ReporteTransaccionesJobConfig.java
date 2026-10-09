package com.bancoxyz.batch.config;

import com.bancoxyz.batch.listener.JobCompletionListener;
import com.bancoxyz.batch.model.Transaccion;
import com.bancoxyz.batch.processor.TransaccionProcessor;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReporteTransaccionesJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TransaccionProcessor transaccionProcessor;
    private final JobCompletionListener jobCompletionListener;

    @Bean
    public Job reporteTransaccionesJob() {
        return new JobBuilder("reporteTransaccionesJob", jobRepository)
                .listener(jobCompletionListener)
                .start(reporteTransaccionesStep())
                .build();
    }

    @Bean
    public Step reporteTransaccionesStep() {
        return new StepBuilder("reporteTransaccionesStep", jobRepository)
                .<Transaccion, Transaccion>chunk(5, transactionManager)
                .reader(transaccionReader())
                .processor(transaccionProcessor)
                .writer(transaccionWriter())
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .skipLimit(10)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public ItemReader<Transaccion> transaccionReader() {
        return new FlatFileItemReaderBuilder<Transaccion>()
                .name("transaccionReader")
                .resource(new ClassPathResource("data/transacciones.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("id", "fecha", "monto", "tipo")
                .targetType(Transaccion.class)
                .build();
    }

    @Bean
    public ItemWriter<Transaccion> transaccionWriter() {
        return items -> {
            List<Transaccion> anomalias = new ArrayList<>();
            for (Transaccion t : items) {
                if (t.isAnomalia()) {
                    anomalias.add(t);
                }
            }
            log.info("Procesadas {} transacciones. Anomalías detectadas: {}",
                    items.size(), anomalias.size());
            anomalias.forEach(a -> log.warn(" ID {} - {} - {}",
                    a.getId(), a.getMonto(), a.getMotivoAnomalia()));
        };
    }
}