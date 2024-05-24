package com.java.entity;

import org.springframework.data.annotation.Id;


public class Product {

    @Id
    public String id;

    public String name;
    public String price;

    public Product() {}

    public Product(String name, String price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format(
                "Product[id=%s, name='%s', price='%s']",
                id, name, price);
    }

}