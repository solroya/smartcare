package com.nado.smartcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class SmartcareApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartcareApplication.class, args);
    }

}
