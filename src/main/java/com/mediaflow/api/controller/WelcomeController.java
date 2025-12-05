package com.mediaflow.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public Map<String, Object> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bienvenido a MediaFlow API");
        response.put("version", "1.0.0");
        response.put("status", "online");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("documentation", "/swagger-ui.html");
        endpoints.put("graphql", "/graphiql");
        endpoints.put("register", "POST /api/v1/users/register");
        endpoints.put("login", "POST /api/v1/users/login");
        
        response.put("endpoints", endpoints);
        
        Map<String, String> instructions = new HashMap<>();
        instructions.put("step1", "Primero registra un usuario en POST /api/v1/users/register");
        instructions.put("step2", "Luego inicia sesión en POST /api/v1/users/login");
        instructions.put("step3", "Copia el token JWT de la respuesta");
        instructions.put("step4", "En Swagger, haz clic en 'Authorize' y pega el token");
        instructions.put("step5", "Ahora puedes acceder a los endpoints protegidos");
        
        response.put("instructions", instructions);
        
        return response;
    }

    @GetMapping("/api")
    public Map<String, Object> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "MediaFlow API");
        response.put("version", "1.0.0");
        response.put("description", "API REST para gestión de contenido multimedia");
        
        Map<String, String> authentication = new HashMap<>();
        authentication.put("type", "JWT Bearer Token");
        authentication.put("header", "Authorization: Bearer {token}");
        authentication.put("loginEndpoint", "POST /api/v1/users/login");
        
        response.put("authentication", authentication);
        
        return response;
    }
}
