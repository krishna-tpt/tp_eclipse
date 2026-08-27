#!/usr/bin/env bash
# Linux/macOS launcher for InventorySimulator.
# Reads defaults from local.properties (copy sample-config.properties first).
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
JAR="$HERE/target/inventory-test-data-generator.jar"
LIB="$HERE/target/lib/*"

[[ -f "$JAR" ]] || { echo "Build first: mvn -q clean package"; exit 1; }
[[ -f "$HERE/local.properties" ]] || { echo "local.properties not found. Copy sample-config.properties to local.properties and edit it."; exit 1; }

exec java -cp "$JAR:$LIB" com.michelin.inventorytest.InventorySimulator \
     --config "$HERE/local.properties" "$@"
