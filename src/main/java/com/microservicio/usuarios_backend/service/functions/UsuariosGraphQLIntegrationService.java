package com.microservicio.usuarios_backend.service.functions;

import java.nio.charset.StandardCharsets;
import java.io.InputStream;
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

    private final String functionUrl = "https://grupo2.azurewebsites.net/api/usuariosPorPerfilGraphQL";

    public UsuariosGraphQLIntegrationService() {
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    public List<Usuario> obtenerUsuariosPorPerfil(String nombrePerfil) {
        try {
            // Cargar la consulta desde el archivo .graphql
            String rawQuery = cargarQueryDesdeArchivo("graphql/usuariosPorPerfil.graphql");
            String query = String.format(rawQuery, nombrePerfil);
            
            Map<String, String> payload = Map.of("query", query);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

            //Enviar POST a Azure Function
            ResponseEntity<String> response = restTemplate.postForEntity(functionUrl, entity, String.class);

            //Respuesta
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode usuariosNode = root.path("data").path("usuariosPorPerfil");

            //Mapear y retornar resultado
            return mapper.readerForListOf(Usuario.class).readValue(usuariosNode);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private String cargarQueryDesdeArchivo(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("No se encontró el archivo: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error al leer archivo GraphQL: " + path, e);
        }
    }
}