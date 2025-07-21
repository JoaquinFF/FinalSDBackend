package com.jff.auth0demo.dto;

public class CrearPeliculaRequest {
    private String titulo;
    private String director;
    private Integer año;
    private String genero;
    private String descripcion;
    private boolean esPublica = true; // Por defecto es pública

    // Constructor vacío
    public CrearPeliculaRequest() {}

    // Constructor
    public CrearPeliculaRequest(String titulo, String director, Integer año, String genero, String descripcion, boolean esPublica) {
        this.titulo = titulo;
        this.director = director;
        this.año = año;
        this.genero = genero;
        this.descripcion = descripcion;
        this.esPublica = esPublica;
    }

    // Getters y Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getAño() {
        return año;
    }

    public void setAño(Integer año) {
        this.año = año;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEsPublica() {
        return esPublica;
    }

    public void setEsPublica(boolean esPublica) {
        this.esPublica = esPublica;
    }
} 