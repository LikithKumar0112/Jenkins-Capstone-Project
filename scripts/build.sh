#!/usr/bin/env bash
#
# build.sh - Clean, test and package the application.
#
set -euo pipefail

# Resolve the project root (parent of the scripts directory).
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

echo "==> Cleaning, testing and packaging the project..."
mvn clean test package

echo "==> Build completed successfully."
