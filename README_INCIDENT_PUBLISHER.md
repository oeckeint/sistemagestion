# Incident Publisher local (Java 8)

Este proyecto implementa internamente el equivalente funcional de `c4e-event-publisher` + `c4e-i18n-core` para evitar incompatibilidades de versión y mantener `Java 8`.

## Paquetes principales

- `app.incidents.contract`: contrato del evento (`IncidentEvent`) y enums (`IncidentType`, `IncidentCategory`, etc.).
- `app.incidents.publisher`: contrato `Publisher`, implementación `IncidentPublisher`, propiedades y excepción.
- `app.common.i18n`: utilidades i18n comunes (`Messages`, `MessageKey`).
- `app.incidents.publisher`: incluye `IncidentPublisherMessageKey` para mensajes del publisher.
- `common.publisher.incident.config.IncidentPublisherConfig`: configuración Spring clásica (no Boot auto-config).

## i18n por bundles

- `i18n/messages_common.properties`: bundle por defecto para cualquier `MessageKey` común.
- `i18n/messages_incidents.properties`: bundle específico del dominio de incidentes.
- `IncidentPublisherMessageKey` sobreescribe `bundle()` para resolver en `i18n.messages_incidents`.

## Publicar desde código legacy

```java


IncidentPublisherServiceBridge.publishSystemError("Error procesando XML","xmlHelper.procesarFactura");
```

## Integración automática incluida

`utileria.ArchivoTexto.escribirError(...)` ya invoca `IncidentPublisherServiceBridge.publishSystemError(...)`.

## Configuración

Se define en `src/main/resources/config/application.yml`:

- `spring.rabbitmq.*`
- `app.incident-publisher.*`
- `c4e.incidents.types.*`

Los defaults están alineados con `c4e-records-api` (`incident.<type>.exchange`, `incident.<type>.key`, etc.).

