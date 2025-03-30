package com.microservicio.usuarios_backend.repository;

import com.microservicio.usuarios_backend.model.entities.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Integer> {
    
}