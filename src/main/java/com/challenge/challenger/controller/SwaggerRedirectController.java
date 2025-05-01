package com.challenge.challenger.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SwaggerRedirectController {

    @GetMapping("/")
    public Map<String, String> welcome() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Bienvenido a la API Challenger");
        response.put("swagger", "/swagger-ui/index.html");
        response.put("api_docs", "/v3/api-docs");
        response.put("calculations", "/api/calculations");
        response.put("history", "/api/history");
        return response;
    }
}
