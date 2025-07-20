package com.jff.auth0demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
public class HelloController {

    @Autowired
    private PeliculaService peliculaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String hello() {
        return "¡Bienvenido a la API de Películas! 🎬";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Este es un endpoint público 🌐";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "Este es un endpoint protegido 🔐";
    }

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
     * Endpoint privado que muestra las películas privadas del usuario autenticado
     */
    @GetMapping("/private/peliculas")
    public ResponseEntity<List<Pelicula>> obtenerPeliculasPrivadas(@AuthenticationPrincipal OAuth2User principal) {
        String userEmail = obtenerEmailUsuario(principal);
        
        // Crear o obtener el usuario
        String nombre = principal.getAttribute("name");
        if (nombre == null) nombre = userEmail;
        usuarioService.obtenerOCrearUsuario(userEmail, nombre);
        
        List<Pelicula> peliculasPrivadas = peliculaService.obtenerPeliculasPrivadasDelUsuario(userEmail);
        return ResponseEntity.ok(peliculasPrivadas);
    }

    /**
     * Endpoint alternativo para JWT (resource server)
     */
    @GetMapping("/private/peliculas-jwt")
    public ResponseEntity<List<Pelicula>> obtenerPeliculasPrivadasJwt(@AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");
        if (userEmail == null) {
            userEmail = jwt.getSubject();
        }
        
        List<Pelicula> peliculasPrivadas = peliculaService.obtenerPeliculasPrivadasDelUsuario(userEmail);
        return ResponseEntity.ok(peliculasPrivadas);
    }

    /**
     * Endpoint para que usuarios agreguen películas públicas a su lista privada
     */
    @PostMapping("/private/peliculas/agregar/{peliculaId}")
    public ResponseEntity<Map<String, Object>> agregarPeliculaPrivada(@PathVariable Long peliculaId, 
                                                                     @AuthenticationPrincipal OAuth2User principal) {
        String userEmail = obtenerEmailUsuario(principal);
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
                                                                     @AuthenticationPrincipal OAuth2User principal) {
        String userEmail = obtenerEmailUsuario(principal);
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
            null, // Propietario null para películas públicas
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
     * Endpoint para admin ver todos los usuarios
     */
    @GetMapping("/admin/usuarios")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Endpoint para que cualquier usuario autenticado cree una película privada
     */
    @PostMapping("/private/peliculas/crear")
    public ResponseEntity<Map<String, Object>> crearPeliculaPrivada(@RequestBody CrearPeliculaRequest request, 
                                                                   @AuthenticationPrincipal OAuth2User principal) {
        String userEmail = obtenerEmailUsuario(principal);
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
        
        // Crear la película privada
        Pelicula nuevaPelicula = new Pelicula(
            null, // ID se asignará automáticamente
            request.getTitulo(),
            request.getDirector(),
            request.getAño(),
            request.getGenero() != null ? request.getGenero() : "Personal",
            request.getDescripcion() != null ? request.getDescripcion() : "Película personal",
            userEmail, // Propietario es el usuario actual
            false // Siempre privada
        );
        
        Pelicula peliculaCreada = peliculaService.crearPelicula(nuevaPelicula);
        
        respuesta.put("mensaje", "Película privada creada exitosamente");
        respuesta.put("pelicula", peliculaCreada);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Endpoint para obtener información del usuario actual
     */
    @GetMapping("/private/usuario")
    public ResponseEntity<Map<String, Object>> obtenerInfoUsuario(@AuthenticationPrincipal OAuth2User principal) {
        String userEmail = obtenerEmailUsuario(principal);
        String nombre = principal.getAttribute("name");
        if (nombre == null) nombre = userEmail;
        
        Usuario usuario = usuarioService.obtenerOCrearUsuario(userEmail, nombre);
        
        Map<String, Object> info = new HashMap<>();
        info.put("email", userEmail);
        info.put("nombre", nombre);
        info.put("roles", usuario.getRoles());
        info.put("esAdmin", usuario.esAdmin());
        info.put("cantidadPeliculasPrivadas", usuario.getPeliculasPrivadasIds().size());
        
        return ResponseEntity.ok(info);
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
