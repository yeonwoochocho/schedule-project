package com.example.scheduleproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ScheduleProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleProjectApplication.class, args);
    }
}

