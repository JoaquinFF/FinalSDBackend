package com.jff.auth0demo.service;

import org.springframework.stereotype.Service;

import com.jff.auth0demo.model.Usuario;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UsuarioService {
    
    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    public UsuarioService() {
        // Crear algunos usuarios de ejemplo
        // Admin
        Usuario admin = new Usuario("admin@example.com", "Administrador", Arrays.asList("ADMIN"));
        usuarios.put("admin@example.com", admin);
        
        // Usuario normal
        Usuario usuario = new Usuario("user@example.com", "Usuario Demo", Arrays.asList("USER"));
        usuarios.put("user@example.com", usuario);
    }

    /**
     * Obtiene o crea un usuario basado en el email
     */
    public Usuario obtenerOCrearUsuario(String email, String nombre) {
        return usuarios.computeIfAbsent(email, k -> {
            // Por defecto, todos los usuarios nuevos tienen rol USER
            return new Usuario(email, nombre, Arrays.asList("USER"));
        });
    }

    /**
     * Obtiene un usuario por email
     */
    public Usuario obtenerUsuario(String email) {
        return usuarios.get(email);
    }

    /**
     * Agrega una película pública a la lista privada de un usuario
     */
    public boolean agregarPeliculaPrivada(String email, Long peliculaId) {
        Usuario usuario = obtenerUsuario(email);
        if (usuario != null) {
            usuario.agregarPeliculaPrivada(peliculaId);
            return true;
        }
        return false;
    }

    /**
     * Remueve una película de la lista privada de un usuario
     */
    public boolean removerPeliculaPrivada(String email, Long peliculaId) {
        Usuario usuario = obtenerUsuario(email);
        if (usuario != null) {
            usuario.removerPeliculaPrivada(peliculaId);
            return true;
        }
        return false;
    }

    /**
     * Obtiene todas las películas privadas de un usuario (incluyendo las que agregó de la lista pública)
     */
    public List<Long> obtenerPeliculasPrivadasIds(String email) {
        Usuario usuario = obtenerUsuario(email);
        return usuario != null ? new ArrayList<>(usuario.getPeliculasPrivadasIds()) : new ArrayList<>();
    }
} 