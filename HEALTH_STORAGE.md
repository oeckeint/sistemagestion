# Health Check de Storage

Este documento describe el endpoint de estado de almacenamiento de `SistemaGestion`.

## Endpoint

- Metodo: `GET`
- Ruta: `/sistemagestion/health/storage`

## Objetivo

Validar que las rutas de almacenamiento configuradas existan, sean directorios y tengan permisos de escritura.

## Rutas verificadas

- `peajes`
- `peajes.procesados`
- `peajes.remesa`
- `peajes.archivarfactura`
- `peajes.ftp`
- `sql.backup`
- `sql.restauraciones`
- `scripts.ruta`

## Criterio de estado

- `UP`: todas las rutas cumplen (`exists=true`, `directory=true`, `writable=true`)
- `DOWN`: al menos una ruta no cumple

## Codigos HTTP

- `200 OK` cuando estado global es `UP`
- `503 Service Unavailable` cuando estado global es `DOWN`

## Respuesta JSON (ejemplo)

```json
{
  "status": "UP",
  "component": "storage",
  "checkedAt": "2026-03-21T18:20:44.123Z",
  "paths": {
    "peajes": {
      "path": "/usr/local/tomcat/resources/Peajes",
      "exists": true,
      "directory": true,
      "writable": true,
      "healthy": true,
      "detail": "OK"
    }
  }
}
```

## Pruebas rapidas

```bash
curl -i http://localhost:7001/sistemagestion/health/storage
```

## Nota de despliegue en Docker

El endpoint depende de `path.properties` y de la variable `C4E_STORAGE_ROOT` en el contenedor.
La configuracion recomendada para Docker es:

- `C4E_STORAGE_ROOT=/usr/local/tomcat/resources`
- Bind mount del host a `/usr/local/tomcat/resources`

