package com.project.team5backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = "package com.project.team5backend.domain.user.entity")

public class Team4BackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(Team4BackEndApplication.class, args);
    }

}
