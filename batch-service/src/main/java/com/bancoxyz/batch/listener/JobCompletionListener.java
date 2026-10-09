package com.bancoxyz.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class JobCompletionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Iniciando job: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            long durationMs = Duration.between(
                    jobExecution.getStartTime(),
                    jobExecution.getEndTime()
            ).toMillis();
            log.info("Job {} completado exitosamente en {} ms",
                    jobExecution.getJobInstance().getJobName(), durationMs);
        } else {
            log.error("Job {} falló con estado: {}",
                    jobExecution.getJobInstance().getJobName(),
                    jobExecution.getStatus());
        }
    }
}