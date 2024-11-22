package com.microservicio.usuarios_backend.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "perfil")
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private int idPerfil;
    private String nombre;

    @OneToMany(targetEntity = Usuario.class, fetch = FetchType.LAZY, mappedBy = "perfil")
    private List<Usuario> listaUsuarios;

    public int getIdPerfil() {
        return idPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}


