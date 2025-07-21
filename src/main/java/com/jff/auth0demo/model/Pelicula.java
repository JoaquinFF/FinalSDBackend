package com.jff.auth0demo.model;

public class Pelicula {
    private Long id;
    private String titulo;
    private String director;
    private int año;
    private String genero;
    private String descripcion;
    private String propietario;
    private boolean esPublica;

    public Pelicula(Long id, String titulo, String director, int año, String genero, String descripcion, String propietario, boolean esPublica) {
        this.id = id;
        this.titulo = titulo;
        this.director = director;
        this.año = año;
        this.genero = genero;
        this.descripcion = descripcion;
        this.propietario = propietario;
        this.esPublica = esPublica;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    
    public int getAño() { return año; }
    public void setAño(int año) { this.año = año; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getPropietario() { return propietario; }
    public void setPropietario(String propietario) { this.propietario = propietario; }
    
    public boolean isEsPublica() { return esPublica; }
    public void setEsPublica(boolean esPublica) { this.esPublica = esPublica; }
} 