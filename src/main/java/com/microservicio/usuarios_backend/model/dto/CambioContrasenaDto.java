package com.microservicio.usuarios_backend.model.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CambioContrasenaDto {
    private String nuevaContrasena;
}

