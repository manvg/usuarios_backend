package com.microservicio.usuarios_backend.service.perfil;

import com.microservicio.usuarios_backend.model.dto.ResponseModel;
import com.microservicio.usuarios_backend.model.entities.Perfil;
import com.microservicio.usuarios_backend.repository.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PerfilServiceImpl implements PerfilService {

    @Autowired
    private PerfilRepository perfilRepository;

    @Override
    public List<Perfil> getAllPerfiles() {
        return perfilRepository.findAll();
    }

    @Override
    public Optional<Perfil> getPerfilById(Integer idPerfil) {
        return perfilRepository.findById(idPerfil);
    }

    @Override
    public ResponseModel crearPerfil(Perfil perfil) {
        var nuevoPerfil = perfilRepository.save(perfil);
        if (nuevoPerfil.getIdPerfil() > 0) {
            return new ResponseModel(true, "Perfil creado con éxito. Id: " + nuevoPerfil.getIdPerfil());
        } else {
            return new ResponseModel(false, "Error al crear el perfil.");
        }
    }

    @Override
    public ResponseModel actualizarPerfil(Integer id, Perfil perfil) {
        var perfilExistente = perfilRepository.findById(id);
        if (perfilExistente.isPresent()) {
            Perfil p = perfilExistente.get();
            p.setNombre(perfil.getNombre());
            perfilRepository.save(p);
            return new ResponseModel(true, "Perfil actualizado con éxito. Id: " + id);
        } else {
            return new ResponseModel(false, "Perfil no encontrado. Id: " + id);
        }
    }

    @Override
    public ResponseModel eliminarPerfil(Integer id) {
        if (perfilRepository.existsById(id)) {
            perfilRepository.deleteById(id);
            return new ResponseModel(true, "Perfil eliminado con éxito.");
        } else {
            return new ResponseModel(false, "El perfil ingresado no existe.");
        }
    }

}