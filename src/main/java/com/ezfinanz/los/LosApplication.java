package com.ezfinanz.los;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LosApplication {

    public static void main(String[] args) {
        SpringApplication.run(LosApplication.class, args);
        System.out.println("🚀 EZFinanz LOS Backend is Running! 🚀");
    }
}
