package com.jff.auth0demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.jff.auth0demo.dto.ActualizarPeliculaRequest;
import com.jff.auth0demo.dto.CrearPeliculaRequest;
import com.jff.auth0demo.model.Pelicula;
import com.jff.auth0demo.service.PeliculaService;
import com.jff.auth0demo.service.UsuarioService;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
public class HelloController {

    @Autowired
    private PeliculaService peliculaService;

    @Autowired
    private UsuarioService usuarioService;

    // ==================== ENDPOINTS DE PELÍCULAS ====================

    /**
     * Endpoint público que muestra todas las películas públicas
     */
    @GetMapping("/public/peliculas")
    public ResponseEntity<List<Pelicula>> obtenerPeliculasPublicas() {
        List<Pelicula> peliculasPublicas = peliculaService.obtenerPeliculasPublicas();
        return ResponseEntity.ok(peliculasPublicas);
    }

    /**
     * Endpoint privado que muestra las películas públicas que el usuario ha agregado a su lista privada
     */
    @GetMapping("/private/peliculas")
    public ResponseEntity<List<Pelicula>> obtenerPeliculasPrivadas(@AuthenticationPrincipal Object principal) {
        String userEmail;
        String nombre;
        
        if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            userEmail = obtenerEmailUsuario(oauth2User);
            nombre = oauth2User.getAttribute("name");
            if (nombre == null) nombre = userEmail;
        } else if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            userEmail = jwt.getClaimAsString("email");
            if (userEmail == null) {
                userEmail = jwt.getSubject();
            }
            nombre = jwt.getClaimAsString("name");
            if (nombre == null) nombre = userEmail;
        } else {
            throw new RuntimeException("Tipo de autenticación no soportado");
        }
        
        // Crear o obtener el usuario
        usuarioService.obtenerOCrearUsuario(userEmail, nombre);
        
        List<Pelicula> peliculasPrivadas = peliculaService.obtenerPeliculasPrivadasDelUsuario(userEmail);
        return ResponseEntity.ok(peliculasPrivadas);
    }

    /**
     * Endpoint para que usuarios agreguen películas públicas a su lista privada
     */
    @PostMapping("/private/peliculas/agregar/{peliculaId}")
    public ResponseEntity<Map<String, Object>> agregarPeliculaPrivada(@PathVariable Long peliculaId, 
                                                                     @AuthenticationPrincipal Object principal) {
        String userEmail;
        String nombre;
        
        if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            userEmail = obtenerEmailUsuario(oauth2User);
            nombre = oauth2User.getAttribute("name");
            if (nombre == null) nombre = userEmail;
        } else if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            userEmail = jwt.getClaimAsString("email");
            if (userEmail == null) {
                userEmail = jwt.getSubject();
            }
            nombre = jwt.getClaimAsString("name");
            if (nombre == null) nombre = userEmail;
        } else {
            throw new RuntimeException("Tipo de autenticación no soportado");
        }
        
        // Crear o obtener el usuario
        usuarioService.obtenerOCrearUsuario(userEmail, nombre);
        
        Map<String, Object> respuesta = new HashMap<>();
        
        // Verificar que la película existe y es pública
        if (!peliculaService.peliculaExiste(peliculaId)) {
            respuesta.put("error", "La película no existe");
            return ResponseEntity.badRequest().body(respuesta);
        }
        
        if (!peliculaService.peliculaEsPublica(peliculaId)) {
            respuesta.put("error", "Solo se pueden agregar películas públicas a la lista privada");
            return ResponseEntity.badRequest().body(respuesta);
        }
        
        // Agregar a la lista privada del usuario
        boolean agregado = usuarioService.agregarPeliculaPrivada(userEmail, peliculaId);
        
        if (agregado) {
            respuesta.put("mensaje", "Película agregada a tu lista privada exitosamente");
            respuesta.put("peliculaId", peliculaId);
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("error", "No se pudo agregar la película");
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    /**
     * Endpoint para que usuarios remuevan películas de su lista privada
     */
    @DeleteMapping("/private/peliculas/remover/{peliculaId}")
    public ResponseEntity<Map<String, Object>> removerPeliculaPrivada(@PathVariable Long peliculaId, 
                                                                     @AuthenticationPrincipal Object principal) {
        String userEmail;
        String nombre;
        
        if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            userEmail = obtenerEmailUsuario(oauth2User);
            nombre = oauth2User.getAttribute("name");
            if (nombre == null) nombre = userEmail;
        } else if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            userEmail = jwt.getClaimAsString("email");
            if (userEmail == null) {
                userEmail = jwt.getSubject();
            }
            nombre = jwt.getClaimAsString("name");
            if (nombre == null) nombre = userEmail;
        } else {
            throw new RuntimeException("Tipo de autenticación no soportado");
        }
        
        // Crear o obtener el usuario
        usuarioService.obtenerOCrearUsuario(userEmail, nombre);
        
        Map<String, Object> respuesta = new HashMap<>();
        
        boolean removido = usuarioService.removerPeliculaPrivada(userEmail, peliculaId);
        
        if (removido) {
            respuesta.put("mensaje", "Película removida de tu lista privada exitosamente");
            respuesta.put("peliculaId", peliculaId);
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("error", "No se pudo remover la película");
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    /**
     * Endpoint para admin que permite crear nuevas películas
     */
    @PostMapping("/admin/peliculas")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_admin')")
    public ResponseEntity<Map<String, Object>> crearPelicula(@RequestBody CrearPeliculaRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        
        // Validar datos requeridos
        if (request.getTitulo() == null || request.getTitulo().trim().isEmpty()) {
            respuesta.put("error", "El título es obligatorio");
            return ResponseEntity.badRequest().body(respuesta);
        }
        
        if (request.getDirector() == null || request.getDirector().trim().isEmpty()) {
            respuesta.put("error", "El director es obligatorio");
            return ResponseEntity.badRequest().body(respuesta);
        }
        
        if (request.getAño() == null || request.getAño() < 1888) {
            respuesta.put("error", "El año debe ser válido (mínimo 1888)");
            return ResponseEntity.badRequest().body(respuesta);
        }
        
        // Crear la película
        Pelicula nuevaPelicula = new Pelicula(
            null, // ID se asignará automáticamente
            request.getTitulo(),
            request.getDirector(),
            request.getAño(),
            request.getGenero() != null ? request.getGenero() : "Sin género",
            request.getDescripcion() != null ? request.getDescripcion() : "Sin descripción",
            null, // Propietario null para películas del admin
            request.isEsPublica()
        );
        
        Pelicula peliculaCreada = peliculaService.crearPelicula(nuevaPelicula);
        
        respuesta.put("mensaje", "Película creada exitosamente");
        respuesta.put("pelicula", peliculaCreada);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Endpoint para admin ver todas las películas
     */
    @GetMapping("/admin/peliculas")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<Pelicula>> obtenerTodasLasPeliculas() {
        List<Pelicula> todasLasPeliculas = peliculaService.obtenerTodasLasPeliculas();
        return ResponseEntity.ok(todasLasPeliculas);
    }

    /**
     * Endpoint para admin actualizar una película
     */
    @PutMapping("/admin/peliculas/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_admin')")
    public ResponseEntity<Map<String, Object>> actualizarPelicula(@PathVariable Long id, 
                                                                 @RequestBody ActualizarPeliculaRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        
        // Validar que la película existe
        if (!peliculaService.peliculaExiste(id)) {
            respuesta.put("error", "Película no encontrada");
            return ResponseEntity.notFound().build();
        }
        
        // Validar datos requeridos
        if (request.getTitulo() == null || request.getTitulo().trim().isEmpty()) {
            respuesta.put("error", "El título es obligatorio");
            return ResponseEntity.badRequest().body(respuesta);
        }
        
        if (request.getDirector() == null || request.getDirector().trim().isEmpty()) {
            respuesta.put("error", "El director es obligatorio");
            return ResponseEntity.badRequest().body(respuesta);
        }
        
        if (request.getAño() == null || request.getAño() < 1888) {
            respuesta.put("error", "El año debe ser válido (mínimo 1888)");
            return ResponseEntity.badRequest().body(respuesta);
        }
        
        // Crear objeto película con los datos actualizados
        Pelicula peliculaActualizada = new Pelicula(
            id,
            request.getTitulo(),
            request.getDirector(),
            request.getAño(),
            request.getGenero() != null ? request.getGenero() : "Sin género",
            request.getDescripcion() != null ? request.getDescripcion() : "Sin descripción",
            null, // Propietario null para películas del admin
            request.getEsPublica() != null ? request.getEsPublica() : true
        );
        
        try {
            Pelicula peliculaActualizadaResult = peliculaService.actualizarPelicula(id, peliculaActualizada);
            respuesta.put("mensaje", "Película actualizada exitosamente");
            respuesta.put("pelicula", peliculaActualizadaResult);
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            respuesta.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    /**
     * Endpoint para admin eliminar una película
     */
    @DeleteMapping("/admin/peliculas/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_admin')")
    public ResponseEntity<Map<String, Object>> eliminarPelicula(@PathVariable Long id) {
        Map<String, Object> respuesta = new HashMap<>();
        
        boolean eliminada = peliculaService.eliminarPelicula(id);
        if (eliminada) {
            respuesta.put("mensaje", "Película eliminada exitosamente");
            respuesta.put("peliculaId", id);
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("error", "Película no encontrada");
            return ResponseEntity.notFound().build();
        }
    }

    // Método auxiliar para obtener el email del usuario
    private String obtenerEmailUsuario(OAuth2User principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        String userEmail = principal.getAttribute("email");
        if (userEmail == null) {
            userEmail = principal.getAttribute("sub"); // Fallback al subject si no hay email
        }
        return userEmail;
    }
}
