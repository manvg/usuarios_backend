package com.microservicio.usuarios_backend.model.dto;

import com.microservicio.usuarios_backend.model.entities.Usuario;

public class AuthenticationDto {
    private boolean status;
    private String message;
    private Usuario usuario;

    public AuthenticationDto(boolean status, String message, Usuario usuario) {
        this.status = status;
        this.message = message;
        this.usuario = usuario;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}