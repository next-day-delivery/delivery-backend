package com.nextdaydelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DeliveryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryBackendApplication.class, args);
    }

}
