package com.jff.auth0demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        // Datos de ejemplo
        peliculas.add(new Pelicula(contador.getAndIncrement(), "El Padrino", "Francis Ford Coppola", 1972, "Drama", "Una saga familiar épica sobre el crimen organizado", null, true));
        peliculas.add(new Pelicula(contador.getAndIncrement(), "Inception", "Christopher Nolan", 2010, "Ciencia Ficción", "Un ladrón que roba secretos a través de la tecnología de los sueños", null, true));
        peliculas.add(new Pelicula(contador.getAndIncrement(), "Pulp Fiction", "Quentin Tarantino", 1994, "Crimen", "Historias entrelazadas del bajo mundo criminal", null, true));
        peliculas.add(new Pelicula(contador.getAndIncrement(), "Mi Película Favorita", "Usuario Demo", 2023, "Personal", "Una película especial para mí", "user@example.com", false));
        peliculas.add(new Pelicula(contador.getAndIncrement(), "Recuerdos de Infancia", "Usuario Demo", 2022, "Documental", "Un documental personal", "user@example.com", false));
    }

    public List<Pelicula> obtenerPeliculasPublicas() {
        return peliculas.stream()
                .filter(Pelicula::isEsPublica)
                .collect(Collectors.toList());
    }

    public List<Pelicula> obtenerPeliculasPrivadasDelUsuario(String usuario) {
        List<Pelicula> peliculasPrivadas = new ArrayList<>();
        
        // 1. Obtener películas privadas creadas por el usuario
        List<Pelicula> peliculasCreadas = peliculas.stream()
                .filter(p -> !p.isEsPublica() && Objects.equals(p.getPropietario(), usuario))
                .collect(Collectors.toList());
        peliculasPrivadas.addAll(peliculasCreadas);
        
        // 2. Obtener películas públicas que el usuario agregó a su lista privada
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
} 