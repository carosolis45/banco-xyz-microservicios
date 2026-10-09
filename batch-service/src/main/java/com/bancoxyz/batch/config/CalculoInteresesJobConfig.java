package com.bancoxyz.batch.config;

import com.bancoxyz.batch.listener.JobCompletionListener;
import com.bancoxyz.batch.model.Interes;
import com.bancoxyz.batch.processor.InteresProcessor;
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
public class CalculoInteresesJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final InteresProcessor interesProcessor;
    private final JobCompletionListener jobCompletionListener;

    @Bean
    public Job calculoInteresesJob() {
        return new JobBuilder("calculoInteresesJob", jobRepository)
                .listener(jobCompletionListener)
                .start(calculoInteresesStep())
                .build();
    }

    @Bean
    public Step calculoInteresesStep() {
        return new StepBuilder("calculoInteresesStep", jobRepository)
                .<Interes, Interes>chunk(5, transactionManager)
                .reader(interesReader())
                .processor(interesProcessor)
                .writer(interesWriter())
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean
    public ItemReader<Interes> interesReader() {
        return new FlatFileItemReaderBuilder<Interes>()
                .name("interesReader")
                .resource(new ClassPathResource("data/intereses.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("cuentaId", "nombre", "saldo", "edad", "tipo")
                .targetType(Interes.class)
                .build();
    }

    @Bean
    public ItemWriter<Interes> interesWriter() {
        return items -> {
            log.info(" Calculados {} intereses", items.size());
            for (Interes i : items) {
                log.info("   Cuenta {}: interés {} -> saldo final {}",
                        i.getCuentaId(), i.getInteresCalculado(), i.getSaldoFinal());
            }
        };
    }
}