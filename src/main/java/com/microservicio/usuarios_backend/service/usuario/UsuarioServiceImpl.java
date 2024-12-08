package com.microservicio.usuarios_backend.service.usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservicio.usuarios_backend.model.dto.AuthenticationDto;
import com.microservicio.usuarios_backend.model.dto.ResponseModel;
import com.microservicio.usuarios_backend.model.entities.Usuario;
import com.microservicio.usuarios_backend.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    //---------GET---------//
    @Override
    public List<Usuario> getAllUsuarios(){
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> getUsuarioById(Integer id){
        return usuarioRepository.findById(id);
    }

    //---------POST---------//
    @Override
    public ResponseModel createUsuario(Usuario usuario){
        ResponseModel response;
        var nuevoUsuario = usuarioRepository.save(usuario);
        if (nuevoUsuario.getIdUsuario() > 0) {
            response = new ResponseModel(true, "Usuario creado con éxito. Id: " + usuario.getIdUsuario());
        }else{
            response = new ResponseModel(false, "Se ha producido un error al intentar crear el usuario.");
        }
        return response;
    }

    @Override
    public ResponseModel validarUsuarioPorEmail(String email){
        String message = "";
        boolean status = false;

        var existeEmail = usuarioRepository.findByemail(email);
        if (!existeEmail.isEmpty()) {
            message = "Ya existe un usuario con el email '" + email+ "'";
        }else{
            message = "Puede continuar con la creación del usuario.";
            status = true;
        }
        return new ResponseModel(status, message);
    }

    @Override
    public AuthenticationDto validarLogin(String email, String contrasena){
        boolean status = false;
        String message = "";
        Usuario usuarioResponse = null;

        var usuario = usuarioRepository.findByemail(email);
        if (!usuario.isEmpty()) {
            if (usuario.get().getcontrasena().equals(contrasena)) {
                status = true;
                message = "Login realizado con éxito.";
                usuarioResponse = usuario.get();
            }else{
                message = "Usuario y/o contraseña no válidos.";
            }
        }else{
            message = "No existe un usuario asociado al email " + email;
        }
       

        return new AuthenticationDto(status, message, usuarioResponse);
    }

    //---------PUT---------//
    @Override
    public ResponseModel updateUsuario(Integer id, Usuario objUsuario){
        ResponseModel response;
        var usuarioExiste = usuarioRepository.findById(id);
        if (!usuarioExiste.isEmpty()) {
            Usuario usuario = usuarioExiste.get();
            usuario.setApellidoMaterno(objUsuario.getApellidoMaterno());
            usuario.setApellidoPaterno(objUsuario.getApellidoPaterno());
            usuario.setDireccion(objUsuario.getDireccion());
            usuario.setEmail(objUsuario.getEmail());
            usuario.setNombre(objUsuario.getNombre());
            usuario.setPerfil(objUsuario.getPerfil());
            usuario.setTelefono(objUsuario.getTelefono());
            usuario.setcontrasena(objUsuario.getcontrasena());
            usuario.setIdUsuario(id);
            //Actualizar usuario
            usuarioRepository.save(usuario);
            response = new ResponseModel(true, "Usuario actualizado con éxito. Id: " + id);
        }else{
            response = new ResponseModel(true, "Usuario no encontrado. Id:" + id);
        }
        return response;
    }

    //---------DELETE---------//
    @Override
    public ResponseModel deleteUsuario(Integer id){
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return new ResponseModel(true, "Usuario eliminado con éxito");
        }else{
            return new ResponseModel(false, "El usuario ingresado no existe");
        }
    }
}

