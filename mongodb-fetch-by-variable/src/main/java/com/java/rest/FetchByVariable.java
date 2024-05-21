package com.java.rest;

import com.java.entity.Customer;
import com.java.repository.CustomerRepository;
import com.java.service.FetchByVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FetchByVariable {

    private final CustomerRepository repository;

    private final FetchByVariableService variableService;

    public FetchByVariable(CustomerRepository repository, FetchByVariableService variableService) {
        this.repository = repository;
        this.variableService = variableService;
    }

    @GetMapping("/all")
    List<Customer> all() {
        return repository.findAll();
    }

    @GetMapping("/key/{key}/{value}")
    Map<String, String> keyValue(@PathVariable("key") String key, @PathVariable("value") String value) throws ClassNotFoundException {
        return variableService.findByDynamicKey(key, value);
    }
}
