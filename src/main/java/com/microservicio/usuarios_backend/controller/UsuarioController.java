package com.microservicio.usuarios_backend.controller;

import java.util.List;
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

import com.microservicio.usuarios_backend.model.entities.Usuario;
import com.microservicio.usuarios_backend.service.usuario.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

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

    //---------MÉTODOS DELETE---------//
    //Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUsuario(@PathVariable Integer id){
        var response = usuarioService.deleteUsuario(id);
        if (!response.getStatus()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}


