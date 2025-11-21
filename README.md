# DriveSafe OBD — Minimalist, Production-Ready Android OBD2 Client

DriveSafe OBD is a Jetpack Compose + Kotlin multi-module app that pairs with ELM327-style OBD2 adapters (Bluetooth Classic, BLE, Wi-Fi) to stream SAE/ISO telemetry, run diagnostics, log trips, and present a distraction-free driver HUD. The project is designed with Indian + international vehicles in mind and keeps every feature unlocked (paid SKU).

## Highlights
- **Connectivity:** unified adapter stack with Bluetooth SPP, BLE (Nordic UART), Wi-Fi TCP, USB-ready stubs, and a deterministic command queue with priority and backoff.
- **OBD coverage:** SAE J1979 Mode 01–09 (core PIDs implemented), DTC parsing/clearing, custom PID packs (JSON) with runtime import, ISO-TP aware parser, raw console.
- **UX:** Compose-only UI, minimalist dark theme, HUD mirroring, driver mode gating, accessibility-friendly typography and color ramps.
- **Safety & privacy:** Parked-only destructive actions, permission rationales, on-device encrypted log toggle (XOR seed placeholder for demo), opt-in cloud switch, foreground service for continuous polling.
- **Data:** Room database for vehicles, PID samples, DTC events, dashboards; DataStore-backed settings; encrypted payload blobs, export-friendly CSV hooks.
- **Extensibility:** Mock adapter + log replayer, plugin-friendly telemetry Flow, PID registry + evaluator DSL, raw command console for technicians.
- **Tooling:** Unit tests (protocol + queue), Compose UI test, GitHub Actions CI (lint + tests + build), ktlint/detekt hooks, demo GIFs, hardware checklist.

## Repo layout

```
app/                 # Android application + nav, onboarding, settings, services
core/                # Dispatchers, safety gating, locale, security helpers
data/                # Room schema, repositories, DataStore
obd/                 # Adapter drivers, protocol parsing, command queue, use cases
ui/                  # Compose theme + reusable widgets
features/            # Feature modules (dashboard, diagnostics, logs, performance)
docs/                # Hardware guide, demo GIFs, PID packs, API notes
tests/               # (reserved for future shared test fixtures)
```

## Requirements
- Android Studio Ladybug | 2024.2.1+, JDK 17
- Android SDK 23–35, NDK (optional)
- Physical adapter for full validation or the bundled `MockAdapter`

## Getting started

```bash
./gradlew clean assembleDebug
./gradlew lint ktlintCheck detekt test connectedAndroidTest
```

1. **Mock-only smoke:** install `app-debug.apk`, launch, accept onboarding, pick the mock adapter. Live dashboard + logs will replay synthetic data immediately.
2. **Real adapter:** pair via system Bluetooth / Wi-Fi tether, then use *Adapter Scan → Select adapter* (permission prompts shown). Send `ATI` from Settings → Raw command console to confirm `ELM327` echoed.
3. **Driver mode:** pin Dashboard in HUD mode (Settings → HUD mirror). Only 3–6 widgets update, large numerics (72 sp) with semantic colors.
4. **Diagnostics:** open Diagnostics tab, tap *Read DTCs*. Clearing requires the parked sensor (speed < 1 km/h) and double confirmation.
5. **Logging + replay:** Logs tab → Start logging (prompts for VIN placeholder). After your run, stop logging and review entries; export CSV via Room inspector or upcoming share intent.

## Custom PIDs
- Base pack in `docs/pids/base_pack.json`.
- Import via Settings → *Custom PIDs* (coming soon) or call `ImportCustomPidUseCase`.
- Formulas follow the lightweight evaluator (supports `A`, `B`, `C`, `D`, + - * / parentheses). Example: `((A*256)+B)/4`.

## Tests & CI
- `./gradlew test` — JVM unit tests (expression evaluator, DTC decoder, command queue).
- `./gradlew connectedAndroidTest` — Compose UI test for dashboard.
- GitHub Actions workflow (`.github/workflows/ci.yml`) runs lint, detekt, unit + instrumentation tests, and assembles the debug APK on every push.

## Hardware validation
- Follow `docs/hardware-testing.md` — covers adapter sanity, safety gates, Indian-specific test matrix.
- Demo assets under `docs/demo/` show the acceptance scenarios (pairing, live telemetry, DTC handling, logging + replay, custom PID import).

## Developer notes
- Design decisions, extension points, and roadmap live in `IMPLEMENTATION.md`.
- Raw OBD console lives in Settings; plugin/AIDL hooks can subscribe to `ObdConnectionManager.telemetry()` Flow for reuse by partner apps.
- Logs encrypt by default (XOR seed placeholder) and respect the Settings toggle.

## Roadmap (excerpt)
1. **MVP (this drop):** multi-transport connections, SAE Mode 01 dashboards, DTC read/clear, logging + replay, safety gating, docs.
2. **Polish:** deeper ISO-TP multi-frame merge, manufacturer-specific PID library, OTA PID pack sync, improved encryption (AES/GCM via Keystore).
3. **Advanced:** cloud opt-in with TLS MQTT, plugin SDK/AIDL, actuator tests with safe checklists, Firebase Test Lab regression suites.

Happy hacking & safe driving! Pull requests and feedback are welcome.
