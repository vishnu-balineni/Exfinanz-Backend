package com.ezfinanz.los;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.ezfinanz.los.model")
@EnableJpaRepositories(basePackages = "com.ezfinanz.los.repository")
public class LosApplication {

    public static void main(String[] args) {
        SpringApplication.run(LosApplication.class, args);
        System.out.println("🚀 EZFinanz LOS Backend is Running! 🚀");
    }
}
