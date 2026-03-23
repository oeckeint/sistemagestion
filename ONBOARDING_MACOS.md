# Onboarding macOS - SistemaGestion

**Un solo comando instala y configura TODO.**

```bash
cd /Users/jesus/Development/Com4Energy
chmod +x ./scripts/onboarding.sh
./scripts/onboarding.sh
```

Eso es todo.

## Qué hace

1. ✓ Instala Homebrew (si no está).
2. ✓ Instala mysql-client y lo agrega al PATH.
3. ✓ Instala SDKMAN (si no está).
4. ✓ Instala SDKs desde los `.sdkmanrc` de cada proyecto (por ejemplo Java 8 para `sistemagestion` y Java 17 para servicios C4E).
5. ✓ Instala apps adicionales (meld).
6. ✓ Genera `~/.sistema_gestion_env.sh` con TODAS las variables que necesitas:
   ```bash
   export DB_HOST="127.0.0.1"
   export DB_URL_SGE="jdbc:mysql://127.0.0.1:3306/sge"
   export DB_HOST_DOCKER="database"
   export DB_URL_SGE_DOCKER="jdbc:mysql://database:3306/sge"
   # ... todas las demás
   ```
7. ✓ Agrega `source "$HOME/.sistema_gestion_env.sh"` a `~/.zshrc`.
8. ✓ Actualiza el repo (`git pull --ff-only`, se omite si hay cambios locales).
9. ✓ Levanta el stack Docker de Com4Energy usando `/Users/jesus/Development/Com4Energy/start-sistemagestion-docker.sh` (`docker compose down && build && up`)

## Escapes (opcional, para casos especiales)

| Flag | Usar si... |
|------|-----------|
| `--skip-git` | No quieres que haga `git pull` |
| `--skip-docker` | Solo quieres configurar, sin levantar Docker |

Ejemplos:

```bash
# Solo configuración, sin tocar git ni Docker
./scripts/onboarding.sh --skip-git --skip-docker
```

## Variables generadas

El archivo `~/.sistema_gestion_env.sh` incluye:

**Locales** (tu Mac):
- `DB_HOST=127.0.0.1`
- `DB_URL_SGE=jdbc:mysql://127.0.0.1:3306/sge`
- `RABBITMQ_HOST=127.0.0.1`

**Docker** (para docker-compose):
- `DB_HOST_DOCKER=database`
- `DB_URL_SGE_DOCKER=jdbc:mysql://database:3306/sge`
- `RABBITMQ_HOST_DOCKER=rabbitmq`

**Compartidas** (igual para ambos):
- `DB_USER_SGE=admin`, `DB_PASSWORD_SGE=qexkaq-7bodXo`
- `SFTP_HOST=127.0.0.1`, `SFTP_PORT=21`, `SFTP_USER=user1`
- `C4E_HOST_STORAGE_ROOT=$HOME/Downloads/com4energy`
- `SPRING_PROFILES_ACTIVE=dev`

## Notas

- El onboarding invoca el script maestro en la raiz de Com4Energy: `/Users/jesus/Development/Com4Energy/start-sistemagestion-docker.sh`.
- El onboarding ahora vive en la raiz de Com4Energy (`/Users/jesus/Development/Com4Energy/scripts/onboarding.sh`) y opera sobre el proyecto `sistemagestion`.
- Si `sistemagestion` no esta en la ruta por defecto, puedes usar `C4E_PROJECT_ROOT` para apuntar al proyecto antes de ejecutar onboarding.
- Docker ejecuta `down && build && up` en **foreground** (puedes ver los logs en tiempo real).
  - `down` detiene y elimina contenedores **sin borrar volúmenes ni datos** (preserva todo).
  - `build` construye/actualiza las imágenes.
  - `up` levanta nuevos contenedores con los volúmenes intactos.
- Si necesitas borrar todo (reset destructivo), ejecuta manualmente:
  ```bash
  docker compose down -v
  ```
- Si tu IDE no hereda `~/.zshrc`, ejecuta `source "$HOME/.sistema_gestion_env.sh"` manualmente antes de usarlo.
- El archivo `~/.sistema_gestion_env.sh` se regenera cada vez que ejecutas onboarding, pero solo se actualiza si hay cambios (idempotente).
- El `.env` del repo es solo documentación; la fuente de verdad es `~/.sistema_gestion_env.sh`.
