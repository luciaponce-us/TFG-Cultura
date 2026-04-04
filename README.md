# TFG Cultura ETSII

## Descripción
Desarrollo del Trabajo de Fin de Grado "Aplicación web para la integración de las actividades de la Delegación de Cultura de la ETSII".

## Tecnologías
- Frontend: React + Vite + TypeScript
- Backend: Spring Boot (Java)
- Contenedores: Docker + Docker Compose

## Instalación

### Requisitos

- Git (para clonar el proyecto).
- Docker Desktop (Windows / Mac).
- Docker y Docker Compose (Linux).
- Conexión a Internet (solo para la primera descarga de imágenes).

### 1. Clonar el repositorio
```
git clone https://github.com/luciaponce-us/TFG-Cultura.git
cd TFG-Cultura
```

### 2. Construir y levantar todos los servicios

Desde la raíz del proyecto:
```
docker compose up --build
```

### 3. Acceder a la aplicación

- 🌐 Frontend:
http://localhost:5173
- ⚙️ Backend API:
http://localhost:8080
- 🍃 MongoDB:  

    - 🐳 Desde Docker: `
docker exec -it mongo-local mongosh -u root -p root --authenticationDatabase admin
`
    - 🧭 Conexión con MongoDB Compass: [mongodb://root:root@localhost:27018/?authSource=admin](mongodb://root:root@localhost:27018/?authSource=admin)

## Producción (Mongo Atlas)

Para producción, configura la variable `MONGODB_URI` con la cadena de conexión de Atlas.

Ejemplo:
`mongodb+srv://<user>:<password>@<cluster>.mongodb.net/<database>?retryWrites=true&w=majority`

Notas:
- `MONGODB_URI` tiene prioridad sobre la URI local.
- En local puedes seguir usando Docker Compose con `MONGO_USER`, `MONGO_PASS` y `MONGO_DB`.

## Licencia

Este proyecto está bajo la licencia **CC BY-NC 4.0**.

Puedes usarlo y modificarlo siempre que:
- Des crédito a la autora.
- No lo uses con fines comerciales.

Autora: Lucía Ponce García de Sola

Puedes ver más detalles en el archivo [LICENSE](./LICENSE).