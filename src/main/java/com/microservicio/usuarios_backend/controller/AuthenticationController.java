package com.microservicio.usuarios_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.microservicio.usuarios_backend.service.authentication.AuthenticationService;
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

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginDto) {
        try {
            String email = loginDto.getEmail();
            String password = loginDto.getContrasena();

            //Validar contra base de datos email y contraseña
            AuthenticationDto response = usuarioService.validarLogin(email, password);

            if (response.getStatus()) {
                String token = authenticationService.authenticate(response.getUsuario());
                AuthResponse authResponse = new AuthResponse(true, token, "Autenticación exitosa");

                return ResponseEntity.ok(authResponse);
            } else {
                AuthResponse authResponse = new AuthResponse(false, null, "Correo electrónico y/o contraseña no válidos");

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