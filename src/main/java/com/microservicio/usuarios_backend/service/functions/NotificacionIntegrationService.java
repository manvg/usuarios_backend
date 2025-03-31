package com.microservicio.usuarios_backend.service.functions;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotificacionIntegrationService {
    
    private final RestTemplate restTemplate;

    private final String functionUrl = "https://grupo2.azurewebsites.net/api/NotifCambioRol?";
    //URL para el cambio de contra
    private final String functionUrlContrasena = "https://grupo2.azurewebsites.net/api/NotifCambioContrasena?";

    public NotificacionIntegrationService() {
        this.restTemplate = new RestTemplate();
    }

    public String notificarCambioRol(Map<String, Object> payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(payload);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(functionUrl, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error en la conversión JSON";
        }
    }

    public String notificarCambioContrasena(Map<String, Object> payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(payload);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(functionUrlContrasena, entity, String.class);
            return response.getBody();
        } catch(Exception e) {
            e.printStackTrace();
            return "Error en la conversión JSON";
        }
    }
}