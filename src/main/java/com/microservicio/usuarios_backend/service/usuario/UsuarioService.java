package com.microservicio.usuarios_backend.service.usuario;

import java.util.List;
import java.util.Optional;

import com.microservicio.usuarios_backend.model.dto.AuthenticationDto;
import com.microservicio.usuarios_backend.model.dto.DatosPersonalesDto;
import com.microservicio.usuarios_backend.model.dto.ResponseModel;
import com.microservicio.usuarios_backend.model.entities.Usuario;


public interface UsuarioService {
    List<Usuario> getAllUsuarios();
    Optional<Usuario> getUsuarioById(Integer id);
    ResponseModel validarUsuarioPorEmail(String email);
    ResponseModel createUsuario(Usuario usuario);
    ResponseModel updateUsuario(Integer id, Usuario usuario);
    ResponseModel updateDatosPersonales(Integer id, DatosPersonalesDto datos);
    ResponseModel deleteUsuario(Integer id);
    ResponseModel deleteUsuarioByEmail(String email);
    AuthenticationDto validarLogin(String email, String contrasena);
    ResponseModel cambiarContrasena(Integer idUsuario, String nuevaContrasena);
    List<Usuario> getUsuariosPorPerfil(String nombrePerfil);//GraphQL

}

