#!/usr/bin/env bash
#
# run.sh - Locate the generated jar and run the application.
#
set -euo pipefail

# Resolve the project root (parent of the scripts directory).
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${PROJECT_ROOT}"

# Find the runnable jar, skipping any sources/javadoc artifacts.
JAR_FILE="$(find target -maxdepth 1 -type f -name '*.jar' \
    ! -name '*-sources.jar' ! -name '*-javadoc.jar' 2>/dev/null | head -n 1)"

if [[ -z "${JAR_FILE}" ]]; then
    echo "ERROR: No jar found in target/. Run scripts/build.sh first." >&2
    exit 1
fi

echo "==> Running ${JAR_FILE}"
java -jar "${JAR_FILE}"
