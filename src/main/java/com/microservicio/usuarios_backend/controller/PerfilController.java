package com.microservicio.usuarios_backend.controller;

import com.microservicio.usuarios_backend.model.entities.Perfil;
import com.microservicio.usuarios_backend.service.perfil.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    //---------MÉTODOS GET---------//
    @GetMapping
    public List<Perfil> getAllPerfiles() {
        List<Perfil> perfiles = perfilService.getAllPerfiles();
        return perfiles;
    }

    @GetMapping("/{id}")
    public Optional<Perfil> getPerfilById(@PathVariable Integer id){
        var perfil = perfilService.getPerfilById(id);
        return perfil;
    }
}