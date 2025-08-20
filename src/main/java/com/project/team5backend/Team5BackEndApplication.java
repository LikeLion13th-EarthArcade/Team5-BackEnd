package com.project.team5backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class Team5BackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(Team5BackEndApplication.class, args);
    }

}
