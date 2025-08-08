package com.project.team5backend.global.config;

import com.project.team5backend.domain.user.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SessionConfig {

    @Bean
    public Map<String, User> sessionStore() {
        return new ConcurrentHashMap<>();
    }
}