package com.microservicio.usuarios_backend.service.functions;

import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.usuarios_backend.model.entities.Usuario;

@Service
public class UsuariosGraphQLIntegrationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String functionUrl = "https://<REEMPLAZAR>.azurewebsites.net/api/usuariosPorPerfilGraphQL";

    public UsuariosGraphQLIntegrationService() {
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    public List<Usuario> obtenerUsuariosPorPerfil(String nombrePerfil) {
        try {
            //consulta GraphQL
            String query = String.format("{ usuariosPorPerfil(nombrePerfil: \"%s\") { idUsuario nombre correo } }", nombrePerfil);

            Map<String, String> payload = Map.of("query", query);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

            //Enviar POST a Azure Function
            ResponseEntity<String> response = restTemplate.postForEntity(functionUrl, entity, String.class);

            //Respuesta
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode usuariosNode = root.path("data").path("usuariosPorPerfil");

            //Mapear
            return mapper.readerForListOf(Usuario.class).readValue(usuariosNode);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}