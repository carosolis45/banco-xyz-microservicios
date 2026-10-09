package com.bancoxyz.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final org.springframework.context.ApplicationContext context;

    @Value("${batch.job.to-run:NONE}")
    private String jobToRun;

    @Override
    public void run(String... args) throws Exception {
        if ("NONE".equals(jobToRun)) {
            log.info(" No hay job configurado para ejecutar (batch.job.to-run=NONE)");
            return;
        }

        log.info("🎬 Iniciando ejecución del job: {}", jobToRun);
        Job job = context.getBean(jobToRun, Job.class);

        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(job, params);
    }
}