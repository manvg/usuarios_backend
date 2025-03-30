package com.microservicio.usuarios_backend.service.perfil;

import com.microservicio.usuarios_backend.model.dto.ResponseModel;
import com.microservicio.usuarios_backend.model.entities.Perfil;
import java.util.List;
import java.util.Optional;

public interface PerfilService {
    List<Perfil> getAllPerfiles();
    Optional<Perfil> getPerfilById(Integer idPerfil);
    ResponseModel crearPerfil(Perfil perfil);
    ResponseModel actualizarPerfil(Integer id, Perfil perfil);
    ResponseModel eliminarPerfil(Integer id);
}