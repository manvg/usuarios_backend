package com.microservicio.usuarios_backend.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicio.usuarios_backend.model.dto.DatosPersonalesDto;
import com.microservicio.usuarios_backend.model.dto.ResponseModel;
import com.microservicio.usuarios_backend.model.entities.Usuario;
import com.microservicio.usuarios_backend.service.authentication.AuthenticationService;
import com.microservicio.usuarios_backend.service.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationService authenticationService;

    //---------MÉTODOS GET---------//
    @GetMapping
    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        return usuarios;
    }

    @GetMapping("/{id}")
    public Optional<Usuario> getUsuarioById(@PathVariable Integer id){
        var usuario = usuarioService.getUsuarioById(id);
        return usuario;
    }

    //---------MÉTODOS POST---------//
    @PostMapping
    public ResponseEntity<Object> createUsuario(@RequestBody @Valid Usuario usuario){
        var validacionEmail = usuarioService.validarUsuarioPorEmail(usuario.getEmail());
        if (!validacionEmail.getStatus()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validacionEmail);
        }

        var response = usuarioService.createUsuario(usuario);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //---------MÉTODOS PUT---------//
    //Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<Object>  updateUsuario(@PathVariable Integer id, @RequestBody @Valid Usuario usuario){
        var response = usuarioService.updateUsuario(id, usuario);
        if (response.getStatus()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}/datos-personales")
    public ResponseEntity<Object> actualizarDatosPersonales(@PathVariable Integer id, @RequestBody DatosPersonalesDto datosPersonales) 
    {
        var response = usuarioService.updateDatosPersonales(id, datosPersonales);
        if (response.getStatus()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/cambiar-contrasena")
    public ResponseEntity<Object> cambiarContrasena(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) 
    {
        String nuevaContrasena = request.get("nuevaContrasena");
    
        if (nuevaContrasena == null || nuevaContrasena.isEmpty()) {
            return ResponseEntity.badRequest().body("La nueva contraseña es obligatoria.");
        }
    
        String bearerToken = httpRequest.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token no proporcionado o inválido.");
        }
    
        String token = bearerToken.substring(7);
        
        if (!authenticationService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel(false, "Token inválido"));
        }
    
        //Extraer el ID del usuario desde el token
        Integer idUsuario = authenticationService.getIdFromToken(token);
    
        //Cambiar la contraseña
        ResponseModel response = usuarioService.cambiarContrasena(idUsuario, nuevaContrasena);
    
        if (response.getStatus()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    //---------MÉTODOS DELETE---------//
    //Eliminar usuario
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Object> deleteUsuario(@PathVariable Integer id){
    //     var response = usuarioService.deleteUsuario(id);
    //     if (!response.getStatus()) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    //     }
    //     return ResponseEntity.status(HttpStatus.OK).body(response);
    // }

    //Eliminar usuario por email
    @DeleteMapping("/{email}")
    public ResponseEntity<Object> deleteUsuarioPorEmail(@PathVariable String email){
        var response = usuarioService.deleteUsuarioByEmail(email);
        if (!response.getStatus()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


     //---------MÉTODOS GRAPHQL---------//
    //@GetMapping("/porPerfil")
    // public ResponseEntity<?> getUsuariosPorPerfil(@RequestParam String nombrePerfil) {
    //     try {
    //         var usuarios = usuarioService.getUsuariosPorPerfil(nombrePerfil);
    //         return ResponseEntity.ok(usuarios);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("Error al obtener usuarios por perfil: " + e.getMessage());
    //     }
    // }

}


