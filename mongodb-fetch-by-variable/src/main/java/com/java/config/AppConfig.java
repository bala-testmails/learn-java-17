package com.java.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public Map<String, MongoRepository> repositories(ApplicationContext ctx) {
        return ctx.getBeansOfType(MongoRepository.class);
    }
}
