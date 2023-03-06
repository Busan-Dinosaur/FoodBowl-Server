package com.dinosaur.foodbowl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FoodbowlApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodbowlApplication.class, args);
    }
}
