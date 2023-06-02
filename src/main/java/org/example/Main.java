package org.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Main {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = "https://api.github.com/users/kamilt598/repos";
        ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
        System.out.println(response);
    }
}