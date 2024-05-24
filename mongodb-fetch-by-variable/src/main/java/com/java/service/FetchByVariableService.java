package com.java.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FetchByVariableService {

    @Value("${mongodb.documentName}")
    private String documentName;

    @Value("${mongodb.queries.query}")
    private String dbQuery;

    @Value("${mongodb.queries.params-count}")
    private int dbQueryParamsCount;

    private final MongoTemplate mongoTemplate;

    //private Map<String, MongoRepository> repositories;

    public FetchByVariableService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        //this.repositories = repositories;
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


    public List<?> queryCollection(String collectionName, Map<String, String> queryParams) throws ClassNotFoundException {

        List<String> indexedParams = queryParams.values().stream().limit(dbQueryParamsCount).toList();
        Map<String, String> collectedMap = queryParams.entrySet().stream().skip(dbQueryParamsCount).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        String dbQuery1 = String.format(dbQuery, String.join(", ", indexedParams));
        BasicQuery basicQuery = new BasicQuery(dbQuery1);
        //Query query = new Query();
        collectedMap.forEach((column, value) -> {
            //query.fields().include(column);
            if(value.matches("\\d+(\\.\\d+)?")) {
                if(value.matches("\\d+\\.\\d+")) {
                    basicQuery.addCriteria(Criteria.where(column).is(Double.parseDouble(value)));
                } else {
                    basicQuery.addCriteria(Criteria.where(column).is(Integer.parseInt(value)));
                }
            } else {
                basicQuery.addCriteria(Criteria.where(column).is(value)
                        .regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE))
                        .regex(Pattern.compile("^" + Pattern.quote(value) + "$")));
            }
        });

        return mongoTemplate.find(basicQuery, Class.forName(getFullyQualifiedName(collectionName)));
    }


    public List<?> queryCollection1(String collectionName, Map<String, String> queryParams) throws ClassNotFoundException {
        Map<String, String> result = new HashMap<>();

        BasicQuery basicQuery = new BasicQuery(dbQuery);
        Query query = new Query();
        queryParams.forEach( (column, value) -> {
            //query.fields().include(column);
            query.addCriteria(Criteria.where(column).is(value)
                    .regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE))
                    .regex(Pattern.compile("^" + Pattern.quote(value) + "$")));
        });

        return mongoTemplate.find(query, Class.forName(getFullyQualifiedName(collectionName)));
        /*
        List<?> documents = mongoTemplate.find(query, Class.forName(documentName));

        // Filter document to get only the required fireld
        documents.forEach( document -> {
            Field[] fields = document.getClass().getDeclaredFields();
            Arrays.stream(fields).filter( field -> queryParams.containsKey(field.getName())).forEach(
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
        */
    }

    public String getFullyQualifiedName(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return "com.java.entity." + str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
