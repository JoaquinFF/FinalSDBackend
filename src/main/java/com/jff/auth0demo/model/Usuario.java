package com.jff.auth0demo.model;

import java.util.List;
import java.util.ArrayList;

public class Usuario {
    private String email;
    private String nombre;
    private List<String> roles;
    private List<Long> peliculasPrivadasIds; // IDs de películas públicas que el usuario ha agregado a su lista privada

    public Usuario(String email, String nombre) {
        this.email = email;
        this.nombre = nombre;
        this.roles = new ArrayList<>();
        this.peliculasPrivadasIds = new ArrayList<>();
    }

    public Usuario(String email, String nombre, List<String> roles) {
        this.email = email;
        this.nombre = nombre;
        this.roles = roles != null ? roles : new ArrayList<>();
        this.peliculasPrivadasIds = new ArrayList<>();
    }

    // Getters y Setters no usados por el momentos
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public List<Long> getPeliculasPrivadasIds() { return peliculasPrivadasIds; }
    public void setPeliculasPrivadasIds(List<Long> peliculasPrivadasIds) { this.peliculasPrivadasIds = peliculasPrivadasIds; }

    // Métodos de utilidad
    public boolean tieneRol(String rol) {
        return roles.contains(rol);
    }

    public boolean esAdmin() {
        return tieneRol("ADMIN") || tieneRol("admin");
    }

    public void agregarPeliculaPrivada(Long peliculaId) {
        if (!peliculasPrivadasIds.contains(peliculaId)) {
            peliculasPrivadasIds.add(peliculaId);
        }
    }

    public void removerPeliculaPrivada(Long peliculaId) {
        peliculasPrivadasIds.remove(peliculaId);
    }
} 