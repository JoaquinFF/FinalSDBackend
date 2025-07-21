package com.jff.auth0demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jff.auth0demo.model.Pelicula;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PeliculaService {
    
    private final List<Pelicula> peliculas = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    @Autowired
    private UsuarioService usuarioService;

    public PeliculaService() {
        // Datos de ejemplo - solo películas públicas creadas por admin
        peliculas.add(new Pelicula(contador.getAndIncrement(), "El Padrino", "Francis Ford Coppola", 1972, "Drama", "Una saga familiar épica sobre el crimen organizado", null, true));
        peliculas.add(new Pelicula(contador.getAndIncrement(), "Inception", "Christopher Nolan", 2010, "Ciencia Ficción", "Un ladrón que roba secretos a través de la tecnología de los sueños", null, true));
        peliculas.add(new Pelicula(contador.getAndIncrement(), "Pulp Fiction", "Quentin Tarantino", 1994, "Crimen", "Historias entrelazadas del bajo mundo criminal", null, true));
        peliculas.add(new Pelicula(contador.getAndIncrement(), "El Señor de los Anillos", "Peter Jackson", 2001, "Fantasía", "Una épica aventura en la Tierra Media", null, true));
        peliculas.add(new Pelicula(contador.getAndIncrement(), "Matrix", "Lana y Lilly Wachowski", 1999, "Ciencia Ficción", "Un hacker descubre la verdad sobre la realidad", null, true));
    }

    public List<Pelicula> obtenerPeliculasPublicas() {
        return peliculas.stream()
                .filter(Pelicula::isEsPublica)
                .collect(Collectors.toList());
    }

    public List<Pelicula> obtenerPeliculasPrivadasDelUsuario(String usuario) {
        List<Pelicula> peliculasPrivadas = new ArrayList<>();
        
        // Solo obtener películas públicas que el usuario agregó a su lista privada
        List<Long> peliculasPrivadasIds = usuarioService.obtenerPeliculasPrivadasIds(usuario);
        List<Pelicula> peliculasAgregadas = peliculas.stream()
                .filter(p -> p.isEsPublica() && peliculasPrivadasIds.contains(p.getId()))
                .collect(Collectors.toList());
        peliculasPrivadas.addAll(peliculasAgregadas);
        
        return peliculasPrivadas;
    }

    public Pelicula crearPelicula(Pelicula nuevaPelicula) {
        nuevaPelicula.setId(contador.getAndIncrement());
        peliculas.add(nuevaPelicula);
        return nuevaPelicula;
    }

    public List<Pelicula> obtenerTodasLasPeliculas() {
        return new ArrayList<>(peliculas);
    }

    public Optional<Pelicula> obtenerPeliculaPorId(Long id) {
        return peliculas.stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst();
    }

    public boolean peliculaExiste(Long id) {
        return obtenerPeliculaPorId(id).isPresent();
    }

    public boolean peliculaEsPublica(Long id) {
        return obtenerPeliculaPorId(id)
                .map(Pelicula::isEsPublica)
                .orElse(false);
    }

    /**
     * Actualiza una película existente (solo para admin)
     */
    public Pelicula actualizarPelicula(Long id, Pelicula peliculaActualizada) {
        Optional<Pelicula> peliculaExistente = obtenerPeliculaPorId(id);
        if (peliculaExistente.isPresent()) {
            Pelicula pelicula = peliculaExistente.get();
            pelicula.setTitulo(peliculaActualizada.getTitulo());
            pelicula.setDirector(peliculaActualizada.getDirector());
            pelicula.setAño(peliculaActualizada.getAño());
            pelicula.setGenero(peliculaActualizada.getGenero());
            pelicula.setDescripcion(peliculaActualizada.getDescripcion());
            pelicula.setEsPublica(peliculaActualizada.isEsPublica());
            return pelicula;
        }
        throw new RuntimeException("Película no encontrada con ID: " + id);
    }

    /**
     * Elimina una película (solo para admin)
     */
    public boolean eliminarPelicula(Long id) {
        Optional<Pelicula> pelicula = obtenerPeliculaPorId(id);
        if (pelicula.isPresent()) {
            return peliculas.remove(pelicula.get());
        }
        return false;
    }
} 