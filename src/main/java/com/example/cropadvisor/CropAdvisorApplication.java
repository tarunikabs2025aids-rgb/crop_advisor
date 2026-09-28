package com.example.cropadvisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CropAdvisorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CropAdvisorApplication.class, args);
    }
}
