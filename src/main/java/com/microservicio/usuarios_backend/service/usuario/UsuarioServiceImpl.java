package com.microservicio.usuarios_backend.service.usuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservicio.usuarios_backend.model.dto.AuthenticationDto;
import com.microservicio.usuarios_backend.model.dto.DatosPersonalesDto;
import com.microservicio.usuarios_backend.model.dto.ResponseModel;
import com.microservicio.usuarios_backend.model.entities.Usuario;
import com.microservicio.usuarios_backend.repository.UsuarioRepository;
import com.microservicio.usuarios_backend.service.events.EventGridIntegrationService;
import com.microservicio.usuarios_backend.service.functions.NotificacionIntegrationService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // @Autowired
    // private NotificacionIntegrationService notificacionIntegrationService;

    @Autowired
    private EventGridIntegrationService eventGridIntegrationService;

    
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
            if (usuario.get().getContrasena().equals(contrasena)) {
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
        if (usuarioExiste.isPresent()) {
            Usuario usuario = usuarioExiste.get();

            //capturamos el rol actual antes de la actualizacion
            String oldRole = usuario.getPerfil().getNombre();

            usuario.setApellidoMaterno(objUsuario.getApellidoMaterno());
            usuario.setApellidoPaterno(objUsuario.getApellidoPaterno());
            usuario.setDireccion(objUsuario.getDireccion());
            usuario.setEmail(objUsuario.getEmail());
            usuario.setNombre(objUsuario.getNombre());
            usuario.setPerfil(objUsuario.getPerfil());
            usuario.setTelefono(objUsuario.getTelefono());
            usuario.setContrasena(objUsuario.getContrasena());
            usuario.setIdUsuario(id);

            //Actualizar usuario
            usuarioRepository.save(usuario);

            //capturamos el nuevo rol post-actualizacion
            String newRole = usuario.getPerfil().getNombre();

            //si el rol cambia llamamos a la func de notificacion
            if (!oldRole.equals(newRole)) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("email", usuario.getEmail());
                payload.put("oldRole", oldRole);
                payload.put("newRole", newRole);
                payload.put("userId", usuario.getIdUsuario());

                //Se comenta llamada hacia Azure Function. En su reemplazo se utilizará Event Grid
                //String notificacionResponse = notificacionIntegrationService.notificarCambioRol(payload);
                
                eventGridIntegrationService.notificarCambioRol(payload);

                response = new ResponseModel(true, "Usuario actualizado con éxito. Evento de cambio de rol publicado");
            } else {
                response = new ResponseModel(true, "Usuario actualizado con éxito. Rol sin cambios.");
            }
        }else{
            response = new ResponseModel(true, "Usuario no encontrado. Id:" + id);
        }
        return response;
    }


    @Override
    public ResponseModel updateDatosPersonales(Integer id, DatosPersonalesDto datosPersonales) {
        ResponseModel response;
        var usuarioExiste = usuarioRepository.findById(id);
        if (!usuarioExiste.isEmpty()) {
            Usuario usuario = usuarioExiste.get();
            usuario.setApellidoMaterno(datosPersonales.getApellidoMaterno());
            usuario.setApellidoPaterno(datosPersonales.getApellidoPaterno());
            usuario.setDireccion(datosPersonales.getDireccion());
            usuario.setNombre(datosPersonales.getNombre());
            usuario.setTelefono(datosPersonales.getTelefono());
            usuario.setFechaNacimiento(datosPersonales.getFechaNacimiento());
            usuario.setIdUsuario(id);
            //Actualizar usuario
            usuarioRepository.save(usuario);
            response = new ResponseModel(true, "Datos actualizados con éxito. Id: " + id);
        }else{
            response = new ResponseModel(true, "Usuario no encontrado. Id:" + id);
        }

        return response;
    }

    @Override
    public ResponseModel cambiarContrasena(Integer idUsuario, String nuevaContrasena) {
        var usuarioExiste = usuarioRepository.findById(idUsuario);
        if (!usuarioExiste.isEmpty()) {
            var usuario = usuarioExiste.get();
            usuario.setContrasena(nuevaContrasena);
            usuarioRepository.save(usuario);

            //payload para la funcion
            Map<String, Object> payload = new HashMap<>();
            payload.put("email", usuario.getEmail());
            payload.put("userId", usuario.getIdUsuario());

            //Se comenta llamada hacia Azure Function. En su reemplazo se utilizará Event Grid
            //String notifiacionResponse = notificacionIntegrationService.notificarCambioContrasena(payload);

            // Publicación del evento en Event Grid
            eventGridIntegrationService.notificarCambioContrasena(payload);

            return new ResponseModel(true, "Contraseña actualizada con éxito. Evento publicado.");
        }else{
            return new ResponseModel(false, "Usuario no encontrado");
        }
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

    @Override
    public ResponseModel deleteUsuarioByEmail(String email){
        var usuario = usuarioRepository.findByemail(email);
        if (!usuario.isEmpty()) {
            usuarioRepository.deleteById(usuario.get().getIdUsuario());
            return new ResponseModel(true, "Usuario eliminado con éxito");
        }else{
            return new ResponseModel(false, "El usuario ingresado no existe");
        }
    }

    //---------GRAPHQL---------//

    // @Override
    // public List<Usuario> getUsuariosPorPerfil(String nombrePerfil) {
    //     // Construir la consulta GraphQL como string
    //     String query = String.format("{ usuariosPorPerfil(nombrePerfil: \"%s\") { idUsuario nombre correo } }", nombrePerfil);

    //     Map<String, String> body = Map.of("query", query);

    //     // Cliente HTTP básico con Java 11
    //     try {
    //         var client = java.net.http.HttpClient.newHttpClient();
    //         var request = java.net.http.HttpRequest.newBuilder()
    //                 .uri(new java.net.URI("https://<URL-FUNCTION>.azurewebsites.net/api/usuariosPorPerfilGraphQL")) // ⚠️ REEMPLAZA por tu URL real
    //                 .header("Content-Type", "application/json")
    //                 .POST(java.net.http.HttpRequest.BodyPublishers.ofString(
    //                         new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body)))
    //                 .build();

    //         var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

    //         // Mapear respuesta JSON a lista de Usuario
    //         var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    //         var jsonResponse = mapper.readTree(response.body());

    //         // Obtener nodo "data" → "usuariosPorPerfil"
    //         var usuariosJson = jsonResponse.path("data").path("usuariosPorPerfil");

    //         // Convertir el nodo a lista de objetos Usuario
    //         return mapper.readerForListOf(Usuario.class).readValue(usuariosJson);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return List.of(); // Retorna lista vacía en caso de error
    //     }
    //}

    @Override
    public List<Usuario> getUsuariosPorPerfil(String perfil) {
        return usuarioRepository.findByPerfil_NombreIgnoreCase(perfil);
    }

}

