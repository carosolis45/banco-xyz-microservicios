package com.bancoxyz.transaccion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TransaccionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransaccionServiceApplication.class, args);
    }
}
