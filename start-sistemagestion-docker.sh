#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
USER_ENV_FILE="$HOME/.sistema_gestion_env.sh"
NETWORK_NAME="com4energy_default"
BASE_IMAGE="tomcat:9.0-jdk8-corretto"

# Variables requeridas por docker-compose.yml
REQUIRED_VARS=(
  DB_HOST
  SGE_LOCAL_DB_ROOT_PWD
  SGE_LOCAL_DB_NAME
  DB_URL_SGE
  DB_USER_SGE
  DB_PASSWORD_SGE
  SFTP_HOST
  SFTP_PORT
  SFTP_USER
  SFTP_PASSWORD
  C4E_HOST_STORAGE_ROOT
)

log() {
  printf '[docker-up] %s\n' "$1"
}

fail() {
  printf '[docker-up][error] %s\n' "$1" >&2
  exit 1
}

check_prerequisites() {
  command -v docker >/dev/null 2>&1 || fail "Docker no esta instalado o no esta en PATH."
  docker compose version >/dev/null 2>&1 || fail "Docker Compose (plugin) no esta disponible."
  docker info >/dev/null 2>&1 || fail "Docker daemon no esta corriendo. Inicia Docker Desktop."
  command -v mvn >/dev/null 2>&1 || fail "Maven no esta instalado o no esta en PATH."
  [[ -f "$USER_ENV_FILE" ]] || fail "No existe $USER_ENV_FILE. Ejecuta ./scripts/onboarding.sh primero."
}

check_registry_access() {
  local pull_output

  log "Validando acceso a imagen base Docker (${BASE_IMAGE})..."

  if pull_output=$(docker pull "$BASE_IMAGE" 2>&1 >/dev/null); then
    log "Acceso a Docker Hub OK."
    return
  fi

  if grep -qi "keychain cannot be accessed" <<<"$pull_output"; then
    printf '%s\n' "$pull_output" >&2
    fail "No se puede leer el keychain de macOS en esta sesion. Ejecuta: security -v unlock-keychain \"$HOME/Library/Keychains/login.keychain-db\" y luego repite este script."
  fi

  printf '%s\n' "$pull_output" >&2
  fail "No se pudo descargar ${BASE_IMAGE}. Revisa conectividad, Docker login o credenciales del helper."
}

build_project() {
  cd "$ROOT_DIR"
  log "Construyendo proyecto con Maven (mvn clean install)..."
  mvn clean install
}

load_user_env() {
  # set -a exporta automaticamente todas las variables definidas al hacer source.
  set -a
  # shellcheck disable=SC1090
  source "$USER_ENV_FILE"
  set +a
}

validate_required_vars() {
  local missing=()
  local var value

  for var in "${REQUIRED_VARS[@]}"; do
    value="${!var:-}"
    if [[ -z "$value" ]]; then
      missing+=("$var")
    fi
  done

  if [[ "${#missing[@]}" -gt 0 ]]; then
    printf '[docker-up][error] Faltan variables requeridas:\n' >&2
    printf '  - %s\n' "${missing[@]}" >&2
    fail "Carga/corrige $USER_ENV_FILE y vuelve a ejecutar."
  fi
}

ensure_network() {
  if docker network inspect "$NETWORK_NAME" >/dev/null 2>&1; then
    log "La red Docker $NETWORK_NAME ya existe."
    return
  fi

  log "Creando red Docker externa $NETWORK_NAME..."
  docker network create "$NETWORK_NAME" >/dev/null
}

stop_existing_deployment() {
  cd "$ROOT_DIR"

  log "Deteniendo despliegue previo de SistemaGestion (si existe)..."
  docker compose down --remove-orphans >/dev/null 2>&1 || true

  if docker ps -a --format '{{.Names}}' | grep -qx 'sistemagestion-app'; then
    docker rm -f sistemagestion-app >/dev/null
    log "Contenedor previo sistemagestion-app eliminado."
  else
    log "No hay contenedor previo sistemagestion-app."
  fi
}

start_stack() {
  cd "$ROOT_DIR"

  log "Levantando SistemaGestion con Docker Compose..."
  docker compose up --build -d

  log "Servicios activos:"
  docker compose ps

  log "Tip: valida health con curl http://localhost:7001/sistemagestion/health/storage"
}

main() {
  check_prerequisites
  check_registry_access
  build_project
  load_user_env
  validate_required_vars
  ensure_network
  stop_existing_deployment
  start_stack
}

main "$@"

