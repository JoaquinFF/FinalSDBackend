# API CineApp

---

## 📛 Nombre del Proyecto

API de Películas con Auth0

## 👥 Integrantes del Grupo

- Joaquín Flores Fiorenza

## 📋 Requisitos para su Ejecución

- Java 17 o superior
- Maven 3.8+
- Cuenta en Auth0 y aplicación configurada
- Acceso a Internet

## ⚙️ Instrucciones de Instalación y Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd auth0demo
   ```
2. **Configurar Auth0:**
   - Edita el archivo `src/main/resources/application.properties` con los datos de tu aplicación Auth0 (dominio, clientId, clientSecret, etc).
3. **Compilar el proyecto:**
   ```bash
   ./mvnw clean install
   ```
4. **Ejecutar la aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```
5. **Acceder a la API:**
   - La API estará disponible en `http://localhost:8080`

## 🛠️ Tecnologías Utilizadas

- **Spring Boot**: Framework principal
- **Spring Security**: Seguridad y autenticación
- **Auth0**: Proveedor de identidad
- **OAuth2**: Protocolo de autorización
- **JWT**: Tokens de autenticación

---

## 🎬 Funcionalidades

### Para Todos los Usuarios (Público)
- Ver el catálogo de películas públicas

### Para Usuarios Autenticados
- Ver su lista privada de películas (películas públicas agregadas a su lista)
- Agregar películas públicas a su lista privada
- Remover películas de su lista privada

### Para Administradores
- Crear nuevas películas públicas
- Ver todas las películas del sistema (públicas y privadas)
- Actualizar películas existentes
- Eliminar películas del sistema

## 🔐 Autenticación

La aplicación usa Auth0 para la autenticación. Los endpoints están configurados de la siguiente manera:

- **Públicos**: No requieren autenticación
- **Privados**: Requieren autenticación de usuario y rol de cliente
- **Admin**: Requieren autenticación y rol de administrador

## 📋 Endpoints de la API

### Endpoints Públicos

#### GET `/public/peliculas`
Obtiene todas las películas públicas del catálogo.

**Ejemplo de respuesta:**
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

---

### Endpoints Privados (Requieren autenticación y rol de cliente)

#### GET `/private/peliculas`
Obtiene las películas públicas que el usuario autenticado ha agregado a su lista privada.

**Ejemplo de respuesta:**
```json
[
  {
    "id": 2,
    "titulo": "Inception",
    "director": "Christopher Nolan",
    "año": 2010,
    "genero": "Ciencia Ficción",
    "descripcion": "Un ladrón que roba secretos a través de la tecnología de los sueños",
    "propietario": null,
    "esPublica": true
  }
]
```

#### POST `/private/peliculas/agregar/{peliculaId}`
Agrega una película pública a la lista privada del usuario autenticado.
- Parámetro: `peliculaId` (ID de la película pública)

**Ejemplo de respuesta exitosa:**
```json
{
  "mensaje": "Película agregada a tu lista privada exitosamente",
  "peliculaId": 2
}
```

**Ejemplo de error:**
```json
{
  "error": "La película no existe"
}
```

#### DELETE `/private/peliculas/remover/{peliculaId}`
Remueve una película de la lista privada del usuario autenticado.
- Parámetro: `peliculaId` (ID de la película a remover)

**Ejemplo de respuesta exitosa:**
```json
{
  "mensaje": "Película removida de tu lista privada exitosamente",
  "peliculaId": 2
}
```

**Ejemplo de error:**
```json
{
  "error": "No se pudo remover la película"
}
```

---

### Endpoints de Administrador (Requieren autenticación y rol de administrador)

#### POST `/admin/peliculas`
Crea una nueva película pública.
- Body:
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

**Ejemplo de respuesta:**
```json
{
  "mensaje": "Película creada exitosamente",
  "pelicula": {
    "id": 6,
    "titulo": "Nueva Película",
    "director": "Director Famoso",
    "año": 2024,
    "genero": "Acción",
    "descripcion": "Una película épica",
    "propietario": null,
    "esPublica": true
  }
}
```

#### GET `/admin/peliculas`
Obtiene todas las películas del sistema (públicas y privadas).

**Ejemplo de respuesta:**
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
  },
  // ... otras películas ...
]
```

#### PUT `/admin/peliculas/{id}`
Actualiza una película existente.
- Parámetro: `id` (ID de la película a actualizar)
- Body:
```json
{
  "titulo": "Película Actualizada",
  "director": "Nuevo Director",
  "año": 2022,
  "genero": "Drama",
  "descripcion": "Descripción actualizada",
  "esPublica": true
}
```

**Ejemplo de respuesta:**
```json
{
  "mensaje": "Película actualizada exitosamente",
  "pelicula": {
    "id": 1,
    "titulo": "Película Actualizada",
    "director": "Nuevo Director",
    "año": 2022,
    "genero": "Drama",
    "descripcion": "Descripción actualizada",
    "propietario": null,
    "esPublica": true
  }
}
```

**Ejemplo de error:**
```json
{
  "error": "Película no encontrada"
}
```

#### DELETE `/admin/peliculas/{id}`
Elimina una película del sistema.
- Parámetro: `id` (ID de la película a eliminar)

**Ejemplo de respuesta exitosa:**
```json
{
  "mensaje": "Película eliminada exitosamente",
  "peliculaId": 1
}
```

**Ejemplo de error:**
```json
{
  "error": "Película no encontrada"
}
```