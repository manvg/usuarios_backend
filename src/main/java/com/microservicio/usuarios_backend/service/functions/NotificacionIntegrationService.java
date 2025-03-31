package com.microservicio.usuarios_backend.service.functions;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificacionIntegrationService {
    
    private final RestTemplate restTemplate;

    private final String functionUrl = "https://grupo2.azurewebsites.net/api/NotifCambioRol?";

    public NotificacionIntegrationService() {
        this.restTemplate = new RestTemplate();
    }

    public String notificarCambioRol(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(functionUrl, entity, String.class);
        return response.getBody();

    }

}
