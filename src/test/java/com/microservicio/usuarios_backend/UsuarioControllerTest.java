package com.microservicio.usuarios_backend;

import com.microservicio.usuarios_backend.model.entities.Usuario;
import com.microservicio.usuarios_backend.controller.UsuarioController;
import com.microservicio.usuarios_backend.model.dto.ResponseModel;
import com.microservicio.usuarios_backend.service.usuario.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }

    //---------MÉTODOS GET---------//
    @Test
    void getAllUsuarios_shouldReturnListOfUsuarios() throws Exception {
        List<Usuario> usuariosMock = List.of(new Usuario(), new Usuario());
        when(usuarioService.getAllUsuarios()).thenReturn(usuariosMock);

        mockMvc.perform(get("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(usuarioService, times(1)).getAllUsuarios();
    }

    @Test
    void getUsuarioById_shouldReturnUsuarioIfExists() throws Exception {
        Integer id = 1;
        Usuario usuarioMock = new Usuario();
        when(usuarioService.getUsuarioById(id)).thenReturn(Optional.of(usuarioMock));

        mockMvc.perform(get("/api/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(usuarioService, times(1)).getUsuarioById(id);
    }

    @Test
    void getUsuarioById_shouldReturnNotFoundIfUsuarioNotExists() throws Exception {
        Integer id = 1;
        when(usuarioService.getUsuarioById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(usuarioService, times(1)).getUsuarioById(id);
    }

    //---------MÉTODOS POST---------//
    @Test
    void createUsuario_shouldReturnSuccessIfValid() throws Exception {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@test.com");
        ResponseModel validacionResponse = new ResponseModel(true, "Valid email");
        ResponseModel createResponse = new ResponseModel(true, "Usuario creado con éxito");

        when(usuarioService.validarUsuarioPorEmail(usuarioMock.getEmail())).thenReturn(validacionResponse);
        when(usuarioService.createUsuario(ArgumentMatchers.any(Usuario.class))).thenReturn(createResponse);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"test@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Usuario creado con éxito"));

        verify(usuarioService, times(1)).validarUsuarioPorEmail(usuarioMock.getEmail());
        verify(usuarioService, times(1)).createUsuario(ArgumentMatchers.any(Usuario.class));
    }

    @Test
    void createUsuario_shouldReturnBadRequestIfInvalid() throws Exception {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("test@test.com");
        ResponseModel validacionResponse = new ResponseModel(false, "Email already exists");

        when(usuarioService.validarUsuarioPorEmail(usuarioMock.getEmail())).thenReturn(validacionResponse);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"test@test.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("Email already exists"));

        verify(usuarioService, times(1)).validarUsuarioPorEmail(usuarioMock.getEmail());
        verify(usuarioService, times(0)).createUsuario(ArgumentMatchers.any(Usuario.class));
    }

    //---------MÉTODOS PUT---------//
    @Test
    void updateUsuario_shouldReturnSuccessIfValid() throws Exception {
        Integer id = 1;
        Usuario usuarioMock = new Usuario();
        ResponseModel updateResponse = new ResponseModel(true, "Usuario actualizado con éxito");

        when(usuarioService.updateUsuario(eq(id), ArgumentMatchers.any(Usuario.class))).thenReturn(updateResponse);

        mockMvc.perform(put("/api/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\": \"Usuario actualizado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Usuario actualizado con éxito"));

        verify(usuarioService, times(1)).updateUsuario(eq(id), ArgumentMatchers.any(Usuario.class));
    }

    @Test
    void updateUsuario_shouldReturnBadRequestIfInvalid() throws Exception {
        Integer id = 1;
        Usuario usuarioMock = new Usuario();
        ResponseModel updateResponse = new ResponseModel(false, "Usuario no encontrado");

        when(usuarioService.updateUsuario(eq(id), ArgumentMatchers.any(Usuario.class))).thenReturn(updateResponse);

        mockMvc.perform(put("/api/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\": \"Usuario inválido\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));

        verify(usuarioService, times(1)).updateUsuario(eq(id), ArgumentMatchers.any(Usuario.class));
    }

    //---------MÉTODOS DELETE---------//
    @Test
    void deleteUsuario_shouldReturnSuccessIfValid() throws Exception {
        Integer id = 1;
        ResponseModel deleteResponse = new ResponseModel(true, "Usuario eliminado con éxito");

        when(usuarioService.deleteUsuario(id)).thenReturn(deleteResponse);

        mockMvc.perform(delete("/api/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Usuario eliminado con éxito"));

        verify(usuarioService, times(1)).deleteUsuario(id);
    }

    @Test
    void deleteUsuario_shouldReturnBadRequestIfInvalid() throws Exception {
        Integer id = 1;
        ResponseModel deleteResponse = new ResponseModel(false, "Usuario no encontrado");

        when(usuarioService.deleteUsuario(id)).thenReturn(deleteResponse);

        mockMvc.perform(delete("/api/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));

        verify(usuarioService, times(1)).deleteUsuario(id);
    }
}
