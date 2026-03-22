# Onboarding macOS - SistemaGestion

Script para preparar el entorno local de desarrollo en macOS.

## Que hace

1. Verifica/instala Homebrew.
2. Verifica/instala SDKMAN.
3. Instala y activa versiones del proyecto desde `.sdkmanrc`:
   - Java
   - Maven
   - Tomcat
4. Verifica/instala Docker Desktop (opcional).
5. Configura usuario de Tomcat Manager:
   - Usuario: `Tomcat`
   - Password: `Welcome1`
6. Configura todas las variables de entorno de `sistema_gestion` en `~/.sistema_gestion_env.sh`:
   - DB (`DB_HOST`, `SGE_LOCAL_DB_ROOT_PWD`, `SGE_LOCAL_DB_NAME`, `DB_URL_SGE`, `DB_USER_SGE`, `DB_PASSWORD_SGE`)
   - Storage (`C4E_HOST_STORAGE_ROOT`, `C4E_SCRIPTS_ROOT`)
   - SFTP (`SFTP_HOST`, `SFTP_PORT`, `SFTP_USER`, `SFTP_PASSWORD`)
7. Crea `~/.my.cnf` con credenciales cliente MySQL.
8. Ejecuta `git pull --ff-only` en la **rama actual** (si el repo esta limpio, sin cambios locales).

> Las rutas de usuario se construyen de forma dinamica con `whoami`.

## Uso rapido

```bash
cd "$HOME/Development/Com4Energy/sistemagestion"
chmod +x ./scripts/onboarding.sh
./scripts/onboarding.sh
```

## Opciones

```bash
./scripts/onboarding.sh --dry-run
./scripts/onboarding.sh --non-interactive
./scripts/onboarding.sh --skip-docker
./scripts/onboarding.sh --skip-git-pull
./scripts/onboarding.sh --skip-tomcat-user
./scripts/onboarding.sh --skip-env
./scripts/onboarding.sh --help
```

## Notas

- Si hay cambios locales sin commit, el `git pull` se cancela para evitar conflictos. No cambia de rama: hace pull en la rama en que estés.
- El script crea backup de `tomcat-users.xml` en `tomcat-users.xml.bak`.
- Para aplicar cambios de `tomcat-users.xml`, reinicia Tomcat.
- Con `--dry-run`, solo muestra lo que haria sin instalar ni modificar archivos.
- El script agrega `source "$HOME/.sistema_gestion_env.sh"` a `~/.zshrc` si no existe.
- `docker-compose.yml` toma toda la configuracion sensible directo del entorno del usuario. Si tu terminal/IDE no hereda `~/.zshrc`, ejecuta `source "$HOME/.sistema_gestion_env.sh"` antes de levantar Docker Compose.
- El onboarding exporta `COMPOSE_DISABLE_ENV_FILE=1` para evitar que Docker Compose use valores del `.env` del proyecto por accidente.
- El archivo `.env` del repo queda solo como recordatorio/documentacion local; no es la fuente de verdad del runtime.





