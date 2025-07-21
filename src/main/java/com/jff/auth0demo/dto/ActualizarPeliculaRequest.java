package com.jff.auth0demo.dto;

public class ActualizarPeliculaRequest {
    private String titulo;
    private String director;
    private Integer año;
    private String genero;
    private String descripcion;
    private Boolean esPublica;

    // Constructor vacío
    public ActualizarPeliculaRequest() {}

    // Constructor
    public ActualizarPeliculaRequest(String titulo, String director, Integer año, String genero, String descripcion, Boolean esPublica) {
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

    public Boolean getEsPublica() {
        return esPublica;
    }

    public void setEsPublica(Boolean esPublica) {
        this.esPublica = esPublica;
    }
} 