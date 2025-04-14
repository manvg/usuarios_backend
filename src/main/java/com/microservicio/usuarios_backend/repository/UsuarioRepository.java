package com.microservicio.usuarios_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservicio.usuarios_backend.model.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByemail(String email);
    List<Usuario> findByPerfil_NombreIgnoreCase(String nombrePerfil);
}
