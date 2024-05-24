package com.java.rest;

import com.java.entity.Customer;
import com.java.repository.CustomerRepository;
import com.java.service.FetchByVariableService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/a1/{table}")
    List<?> executeQuery1(@PathVariable("table") String table, @RequestParam Map<String, String> allParams) throws ClassNotFoundException {
        allParams.forEach( (k, v) -> { System.out.println(k + "," + v); });
        return variableService.queryCollection(table, allParams);
    }

    @GetMapping("/{collection}")
    List<?> executeQuery(@PathVariable("collection") String collection, HttpServletRequest request) throws ClassNotFoundException {
        Map<String, String> queryParams = queryParamsInOrder(request.getQueryString());

        queryParams.forEach( (k, v) -> { System.out.println(k + "," + v); });
        return variableService.queryCollection(collection, queryParams);
    }

    public Map<String, String> queryParamsInOrder(String queryString) {
        Map<String, List<String>> params = new LinkedHashMap<>(UriComponentsBuilder.fromUriString("http://localhost/?" + queryString)
                .build()
                .getQueryParams());

        return params.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0), (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }
}
