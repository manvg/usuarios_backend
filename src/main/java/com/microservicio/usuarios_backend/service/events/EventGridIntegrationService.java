package com.microservicio.usuarios_backend.service.events;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class EventGridIntegrationService {

    @Value("${azure.eventgrid.topic-endpoint}")
    private String topicEndpoint;

    @Value("${azure.eventgrid.key}")
    private String accessKey;

    private EventGridPublisherClient<EventGridEvent> client;

    @jakarta.annotation.PostConstruct
    public void init() {
        this.client = new EventGridPublisherClientBuilder()
                .endpoint(topicEndpoint)
                .credential(new AzureKeyCredential(accessKey))
                .buildEventGridEventPublisherClient();
    }

    public void notificarCambioRol(Map<String, Object> payload) {
        publicarEvento("usuario.rol.cambiado", "usuarios/cambio-rol", payload);
    }

    public void notificarCambioContrasena(Map<String, Object> payload) {
        publicarEvento("usuario.contrasena.cambiada", "usuarios/cambio-contrasena", payload);
    }

    public void notificarLoginFallido(Map<String, Object> payload) {
        publicarEvento("usuario.login.fallido.limite", "usuarios/login-fallido", payload);
    }

    private void publicarEvento(String tipoEvento, String subject, Object data) {
        EventGridEvent evento = new EventGridEvent(
                subject,
                tipoEvento,
                BinaryData.fromObject(data),
                "1.0"
        );

        try {
            client.sendEvent(evento);
            System.out.println("Evento publicado: " + tipoEvento);
        } catch (Exception e) {
            System.err.println("Error publicando evento: " + e.getMessage());
        }
    }
}
