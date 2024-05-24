package com.java.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

//@Component
public class RepositoryLoader {

    //@Autowired
    private ApplicationContext ctx;

    //@EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        Map<String, MongoRepository> repos = ctx.getBeansOfType(MongoRepository.class);
        repos.forEach((name, repo) -> System.out.println("Loaded MongoRepository: " + name));
    }

}