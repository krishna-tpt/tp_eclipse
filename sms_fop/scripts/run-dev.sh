#!/usr/bin/env bash
# Local-dev wrapper. Sources .env (if present) into the environment, then
# launches the shaded JAR. Use `scripts/run-dev.sh` from the repo root.
#
# The JAR itself does not parse .env — this script is the bridge for shells
# that don't auto-load it. Kubernetes Deployments inject the same vars via
# `envFrom: [secretRef, configMapRef]` instead (see deploy/k8s/env.example.yaml).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

if [[ -f .env ]]; then
    # `set -a` auto-exports every var assigned while it's on.
    set -a
    # shellcheck disable=SC1091
    source .env
    set +a
    echo "loaded .env"
else
    echo "no .env at repo root — using OS env only (copy .env.example to .env to override)"
fi

JAR="filemanager-core/build/libs/psql-inventory-integration-service-1.0.0.jar"
if [[ ! -f "$JAR" ]]; then
    echo "JAR missing at $JAR — building first"
    ./gradlew -q :filemanager-core:fatJar
fi

: "${APP_PROFILE:=dev}"
export APP_PROFILE

echo "launching with APP_PROFILE=$APP_PROFILE"
exec java -jar "$JAR" "$@"
