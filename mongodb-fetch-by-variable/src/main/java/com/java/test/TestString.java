package com.java.test;

import org.springframework.data.mongodb.core.query.BasicQuery;

public class TestString {

    public static void main(String[] args) {

        String query1 = "{ 'name': '%s', 'price': '%s' }";
        BasicQuery query = new BasicQuery(String.format(query1, "value1", 5));
        System.out.println(String.format(query1, "value1", 5));

    }

}
