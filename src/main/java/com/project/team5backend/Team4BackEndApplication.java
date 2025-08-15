package com.project.team5backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = {"com.project.team5backend.domain.space.space.entity","com.project.team5backend.domain.space.review.entity","com.project.team5backend.domain.user.entity"})
@EnableJpaRepositories(basePackages = {"com.project.team5backend.domain.space.space.repository","com.project.team5backend.domain.space.review.repository","com.project.team5backend.domain.user.repository"})

public class Team4BackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(Team4BackEndApplication.class, args);
    }

}
