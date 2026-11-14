package com.citypulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CitypulseApplication {

        public static void main(String[] args) {
                SpringApplication.run(CitypulseApplication.class, args);
        }

}