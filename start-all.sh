#!/usr/bin/env bash

set -Eeuo pipefail

ENV_NAME="${1:-dev}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_ROOT="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend/angular-app"
LOG_DIR="$ROOT_DIR/logs"

BACKEND_SERVICES=(
  "account-service"
  "api-gateway"
  "auth-service"
  "customer-service"
  "transaction-service"
  "transfer-service"
  "payment-service"
  "report-service"
  "dashboard-service"
)

BACKEND_PIDS=()

require_command() {
  local cmd="$1"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "ERROR: Required command '$cmd' is not installed or not in PATH."
    exit 1
  fi
}

find_jar() {
  local service_dir="$1"

  find "$service_dir/target" -maxdepth 1 -type f -name "*.jar" \
    ! -name "original-*.jar" \
    ! -name "*-sources.jar" \
    ! -name "*-javadoc.jar" \
    ! -name "*-plain.jar" \
    | head -n 1
}

build_and_start_backend() {
  local service="$1"
  local service_dir="$BACKEND_ROOT/$service"
  local log_file="$LOG_DIR/${service}.log"

  if [[ ! -d "$service_dir" ]]; then
    echo "ERROR: Service directory not found: $service_dir"
    exit 1
  fi

  echo
  echo "=================================================="
  echo "Backend service: $service"
  echo "Path: $service_dir"
  echo "=================================================="

  cd "$service_dir"

  echo "Removing target directory..."
  rm -rf target

  echo "Building with Maven..."
  mvn clean install -DskipTests

  local jar_file
  jar_file="$(find_jar "$service_dir")"

  if [[ -z "${jar_file:-}" ]]; then
    echo "ERROR: No runnable JAR found in $service_dir/target"
    exit 1
  fi

  echo "Starting service..."
  echo "JAR: $jar_file"
  echo "Log: $log_file"

  nohup java -jar "$jar_file" > "$log_file" 2>&1 &
  local pid=$!
  BACKEND_PIDS+=("$pid")

  echo "Started $service with PID $pid"
  sleep 5
}

start_frontend() {
  if [[ ! -d "$FRONTEND_DIR" ]]; then
    echo "ERROR: Frontend directory not found: $FRONTEND_DIR"
    exit 1
  fi

  cd "$FRONTEND_DIR"

  if [[ ! -f "package.json" ]]; then
    echo "ERROR: package.json not found in: $FRONTEND_DIR"
    exit 1
  fi

  echo
  echo "=================================================="
  echo "Frontend"
  echo "Path: $FRONTEND_DIR"
  echo "Environment: $ENV_NAME"
  echo "=================================================="

  if npm run | grep -q "build:${ENV_NAME}"; then
    echo "Running frontend build..."
    npm run "build:${ENV_NAME}"
  else
    echo "WARNING: build:${ENV_NAME} not found in package.json"
    echo "Skipping build step."
  fi

  echo "Starting frontend..."
  npm start
}

print_summary() {
  echo
  echo "=================================================="
  echo "Backend started successfully"
  echo "Logs: $LOG_DIR"
  echo "=================================================="

  for i in "${!BACKEND_SERVICES[@]}"; do
    echo "${BACKEND_SERVICES[$i]} -> PID ${BACKEND_PIDS[$i]} -> $LOG_DIR/${BACKEND_SERVICES[$i]}.log"
  done

  echo "=================================================="
}

cleanup() {
  echo
  echo "Stopping backend and frontend services..."
  for pid in "${BACKEND_PIDS[@]:-}"; do
    if kill -0 "$pid" >/dev/null 2>&1; then
      kill "$pid" >/dev/null 2>&1 || true
    fi
  done
}

trap cleanup EXIT INT TERM

main() {
  mkdir -p "$LOG_DIR"

  require_command java
  require_command mvn
  require_command npm

  echo "Root directory: $ROOT_DIR"
  echo "Backend root: $BACKEND_ROOT"
  echo "Frontend dir: $FRONTEND_DIR"
  echo "Environment: $ENV_NAME"

  echo
  echo "Starting backend first..."
  for service in "${BACKEND_SERVICES[@]}"; do
    build_and_start_backend "$service"
  done

  print_summary

  echo
  echo "Starting frontend last..."
  start_frontend
}

main "$@"
