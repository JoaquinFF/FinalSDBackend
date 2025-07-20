# API de Películas con Auth0

Esta es una API REST para gestionar películas con autenticación y autorización usando Auth0. El sistema soporta dos tipos de usuarios: **usuarios normales** y **administradores**.

## 🎬 Funcionalidades

### Para Todos los Usuarios (Público)
- **Ver películas públicas**: Acceso a todas las películas públicas del catálogo

### Para Usuarios Autenticados
- **Lista privada personal**: Cada usuario tiene su propia lista de películas privadas
- **Agregar películas públicas a lista privada**: Los usuarios pueden agregar películas del catálogo público a su lista personal
- **Crear películas privadas**: Los usuarios pueden crear sus propias películas privadas
- **Remover películas de lista privada**: Los usuarios pueden quitar películas de su lista personal

### Para Administradores
- **Crear películas públicas**: Solo los admins pueden agregar películas al catálogo público
- **Ver todas las películas**: Acceso completo al catálogo (públicas y privadas)
- **Gestionar usuarios**: Ver información de todos los usuarios registrados

## 🔐 Autenticación

La aplicación usa Auth0 para la autenticación. Los endpoints están configurados de la siguiente manera:

- **Públicos**: No requieren autenticación
- **Privados**: Requieren autenticación de usuario
- **Admin**: Requieren autenticación y rol de administrador

## 📋 Endpoints de la API

### Endpoints Públicos

#### GET `/public/peliculas`
Obtiene todas las películas públicas del catálogo.

**Respuesta:**
```json
[
  {
    "id": 1,
    "titulo": "El Padrino",
    "director": "Francis Ford Coppola",
    "año": 1972,
    "genero": "Drama",
    "descripcion": "Una saga familiar épica sobre el crimen organizado",
    "propietario": null,
    "esPublica": true
  }
]
```

### Endpoints Privados (Requieren Autenticación)

#### GET `/private/peliculas`
Obtiene las películas privadas del usuario autenticado (incluye películas creadas por el usuario y películas públicas agregadas a su lista personal).

#### POST `/private/peliculas/agregar/{peliculaId}`
Agrega una película pública a la lista privada del usuario.

**Parámetros:**
- `peliculaId`: ID de la película pública a agregar

**Respuesta:**
```json
{
  "mensaje": "Película agregada a tu lista privada exitosamente",
  "peliculaId": 1
}
```

#### DELETE `/private/peliculas/remover/{peliculaId}`
Remueve una película de la lista privada del usuario.

**Parámetros:**
- `peliculaId`: ID de la película a remover

#### POST `/private/peliculas/crear`
Crea una nueva película privada para el usuario.

**Body:**
```json
{
  "titulo": "Mi Película Favorita",
  "director": "Director Famoso",
  "año": 2023,
  "genero": "Drama",
  "descripcion": "Una película muy especial para mí"
}
```

#### GET `/private/usuario`
Obtiene información del usuario autenticado.

**Respuesta:**
```json
{
  "email": "usuario@example.com",
  "nombre": "Usuario Demo",
  "roles": ["USER"],
  "esAdmin": false,
  "cantidadPeliculasPrivadas": 3
}
```

### Endpoints de Administrador

#### POST `/admin/peliculas`
Crea una nueva película pública (solo para administradores).

**Body:**
```json
{
  "titulo": "Nueva Película",
  "director": "Director Famoso",
  "año": 2024,
  "genero": "Acción",
  "descripcion": "Una película épica",
  "esPublica": true
}
```

#### GET `/admin/peliculas`
Obtiene todas las películas del sistema (públicas y privadas).

#### GET `/admin/usuarios`
Obtiene información de todos los usuarios registrados.

## 🚀 Cómo Usar

### 1. Configuración de Auth0

Asegúrate de que tu aplicación Auth0 esté configurada correctamente en `application.properties`.

### 2. Usuarios de Prueba

El sistema incluye usuarios de ejemplo:
- **Admin**: `admin@example.com` (rol: ADMIN)
- **Usuario**: `user@example.com` (rol: USER)

### 3. Flujo de Uso Típico

1. **Usuario normal**:
   - Ve películas públicas en `/public/peliculas`
   - Agrega películas públicas a su lista privada con `POST /private/peliculas/agregar/{id}`
   - Crea películas privadas con `POST /private/peliculas/crear`
   - Ve su lista privada en `GET /private/peliculas`

2. **Administrador**:
   - Tiene acceso a todas las funcionalidades de usuario
   - Puede crear películas públicas con `POST /admin/peliculas`
   - Puede ver todas las películas con `GET /admin/peliculas`
   - Puede ver todos los usuarios con `GET /admin/usuarios`

## 🔧 Tecnologías

- **Spring Boot**: Framework principal
- **Spring Security**: Seguridad y autenticación
- **Auth0**: Proveedor de identidad
- **OAuth2**: Protocolo de autorización
- **JWT**: Tokens de autenticación

## 📝 Notas Importantes

- Las películas públicas son visibles para todos los usuarios
- Las películas privadas solo son visibles para su propietario
- Los usuarios pueden agregar películas públicas a su lista privada sin modificar la película original
- Solo los administradores pueden crear películas públicas
- Todos los usuarios autenticados pueden crear películas privadas

## 🐛 Solución de Problemas

### Error de Autenticación
- Verifica que tu token de Auth0 sea válido
- Asegúrate de estar autenticado para endpoints privados

### Error de Autorización
- Verifica que tengas el rol correcto para endpoints de administrador
- Los roles se asignan automáticamente: nuevos usuarios tienen rol "USER"

### Error de Validación
- Asegúrate de que todos los campos requeridos estén presentes
- El año debe ser mayor o igual a 1888
- El título y director son obligatorios 