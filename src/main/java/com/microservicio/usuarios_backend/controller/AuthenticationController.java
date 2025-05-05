package com.microservicio.usuarios_backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.microservicio.usuarios_backend.service.authentication.AuthenticationService;
import com.microservicio.usuarios_backend.service.events.EventGridIntegrationService;
import com.microservicio.usuarios_backend.model.dto.AuthResponse;
import com.microservicio.usuarios_backend.model.dto.AuthenticationDto;
import com.microservicio.usuarios_backend.model.dto.LoginDto;
import com.microservicio.usuarios_backend.service.usuario.UsuarioService;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private EventGridIntegrationService eventGridIntegrationService;

    private final Map<String, Integer> intentosFallidos = new ConcurrentHashMap<>();

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginDto) {
        try {
            String email = loginDto.getEmail();
            String password = loginDto.getContrasena();

            AuthenticationDto response = usuarioService.validarLogin(email, password);

            if (response.getStatus()) {
                intentosFallidos.remove(email);

                String token = authenticationService.authenticate(response.getUsuario());
                AuthResponse authResponse = new AuthResponse(true, token, "Autenticación exitosa");

                return ResponseEntity.ok(authResponse);
            } else {
                int nuevosIntentos = intentosFallidos.getOrDefault(email, 0) + 1;
                intentosFallidos.put(email, nuevosIntentos);

                String mensajeError = "Correo electrónico y/o contraseña no válidos";
                if (nuevosIntentos >= 5) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("email", email);
                    payload.put("intentos", nuevosIntentos);
                    payload.put("mensaje", "El usuario alcanzó el límite de intentos fallidos. Por motivos de seguridad la cuenta será bloqueada.");

                    eventGridIntegrationService.notificarLoginFallido(payload);

                    mensajeError = "Correo electrónico y/o contraseña no válidos. Se alcanzó el número de intentos límite, la cuenta ha sido bloqueada. Evento publicado";
                }

                AuthResponse authResponse = new AuthResponse(false, null, mensajeError);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
            }

        } catch (Exception e) {
            AuthResponse authResponse = new AuthResponse(false, null, "Error de autenticación");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody String token) {
        if (authenticationService.validateToken(token)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}