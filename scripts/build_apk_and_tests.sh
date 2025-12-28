#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
GRADLEW="${REPO_ROOT}/gradlew"

VARIANT="debug"
RUN_UNIT_TESTS="true"
RUN_CONNECTED_TESTS="true"
RUN_CLEAN="true"

usage() {
  cat <<'EOF'
Usage: scripts/build_apk_and_tests.sh [options]

Builds the requested APK variant and runs the configured test suites.

Options:
  --variant [debug|release]    Select APK variant (default: debug)
  --skip-unit-tests            Skip JVM unit tests (./gradlew test)
  --skip-connected-tests       Skip connectedAndroidTest even if a device is available
  --no-clean                   Skip the initial ./gradlew clean step
  -h, --help                   Show this help text

Environment:
  GRADLE_ARGS                  Extra args forwarded to ./gradlew (e.g. "-Pci=true")
EOF
}

log() {
  echo "[build] $*"
}

warn() {
  echo "[warn] $*" >&2
}

have_connected_device() {
  if ! command -v adb >/dev/null 2>&1; then
    return 1
  fi

  if adb devices 2>/dev/null | tail -n +2 | grep -q $'\tdevice'; then
    return 0
  fi

  return 1
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --variant)
      if [[ $# -lt 2 ]]; then
        echo "Error: --variant expects an argument" >&2
        exit 1
      fi
      VARIANT="${2,,}"
      shift 2
      ;;
    --skip-unit-tests)
      RUN_UNIT_TESTS="false"
      shift
      ;;
    --skip-connected-tests)
      RUN_CONNECTED_TESTS="false"
      shift
      ;;
    --no-clean)
      RUN_CLEAN="false"
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

case "$VARIANT" in
  debug|release)
    VARIANT_TASK_SUFFIX="${VARIANT^}"
    ;;
  *)
    echo "Error: unsupported variant '$VARIANT' (expected debug or release)" >&2
    exit 1
    ;;
esac

if [[ ! -x "$GRADLEW" ]]; then
  echo "Error: Gradle wrapper not found at $GRADLEW" >&2
  exit 1
fi

cd "$REPO_ROOT"

declare -a gradle_tasks=()

if [[ "$RUN_CLEAN" == "true" ]]; then
  gradle_tasks+=("clean")
fi

gradle_tasks+=(":app:assemble${VARIANT_TASK_SUFFIX}")

if [[ "$RUN_UNIT_TESTS" == "true" ]]; then
  gradle_tasks+=("test")
fi

if [[ "$RUN_CONNECTED_TESTS" == "true" ]]; then
  if have_connected_device; then
    gradle_tasks+=("connectedAndroidTest")
  else
    warn "No connected device/emulator detected; skipping connectedAndroidTest."
  fi
fi

if [[ ${#gradle_tasks[@]} -eq 0 ]]; then
  echo "Error: no Gradle tasks queued to run." >&2
  exit 1
fi

IFS=' ' read -r -a extra_gradle_args <<< "${GRADLE_ARGS:-}"

log "Gradle tasks: ${gradle_tasks[*]}"
"$GRADLEW" --no-daemon --stacktrace "${extra_gradle_args[@]}" "${gradle_tasks[@]}"
