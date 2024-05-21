package com.java.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FetchByVariableService {

    @Value("${mongodb.documentName}")
    private String documentName;

    private final MongoTemplate mongoTemplate;

    public FetchByVariableService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Map<String, String> findByDynamicKey(String key, String value) throws ClassNotFoundException {
        Map<String, String> result = new HashMap<>();

        Query query = new Query();
        query.fields().include(key);
        query.addCriteria(Criteria.where(key).is(value));
        List<?> documents = mongoTemplate.find(query, Class.forName(documentName));

        // Filter for the given variable
        documents.forEach( document -> {
           Field[] fields = document.getClass().getDeclaredFields();
           //Arrays.stream(fields).forEach( field -> System.out.println("-------- " + field.getName().equals(key)));
           //List<Field> list = Arrays.stream(fields).filter( field -> field.getName().equals(key)).toList();
            Arrays.stream(fields).filter( field -> field.getName().equals(key)).forEach(
                    field -> {
                        try {
                            result.put(field.getName(), field.get(document).toString());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        });

        return result;
    }
}
