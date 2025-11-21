# IMPLEMENTATION NOTES

## Architecture
- **Clean layers:** `core` (domain + utilities) ← `obd` / `data` (data sources) ← `features/*` (presentation) ← `app` (composition + navigation).
- **Modularity:** Each feature module ships its own ViewModel + Compose screens, only depending on shared `core`, `data`, `obd`, `ui`.
- **DI:** Hilt modules in `core` and `app` provide dispatchers, app scope, DataStore, Room DB, Bluetooth adapter, etc. Every ViewModel uses constructor injection.
- **Flows everywhere:** Telemetry, connection state, DTCs, preferences, and trip logs are backed by `StateFlow`/`SharedFlow` so screens stay reactive.

## OBD stack
- **Adapters:** `BluetoothObdAdapter`, `BleObdAdapter`, `WifiObdAdapter`, `MockObdAdapter` share the `ObdAdapter` contract. BLE uses Nordic UART UUIDs, Wi-Fi uses sockets, Bluetooth uses RFCOMM SPP UUID. `ObdAdapterFactory` selects the correct driver.
- **Command queue:** `CommandQueue` prioritizes mode 01 PIDs versus background tasks (logging, diagnostics). `ObdConnectionManager` drains the queue with adaptive delays.
- **PID registry:** `PidRegistry` ships SAE Mode 01 essentials and loads JSON packs at runtime. `ExpressionEvaluator` parses formulas with a minimalist shunting-yard implementation (supports `A`–`D`, +/-*/ parentheses).
- **Parsing:** `ObdResponseParser` produces `TelemetryValue` objects consumed by dashboards, `DtcDecoder` parses Mode 03 frames, `DrivingStateMonitor` updates parked/idle/moving states for gating.
- **Trip logging:** `TripLoggingController` subscribes to telemetry, stores PID samples in Room, encrypts raw payload blobs if the toggle is on, and exposes start/stop APIs.

## UX decisions
- **Compose-only:** Material3 + custom theme. Widgets in `ui/` handle speed, RPM gauge, metric cards, sparklines. HUD mirroring + large typography accessible via Settings.
- **Navigation:** Graph flows: Onboarding → Adapter scan → Bottom-bar surfaces (Dashboard, Diagnostics, Logs, Performance, Settings). Settings also embeds the raw OBD console.
- **Safety:** `ParkedActionGate` ensures destructive operations respect the parked requirement. Buttons disable automatically when not safe.
- **Accessibility:** High-contrast palette, >48dp touch targets, large fonts, localized units.

## Data & security
- **Room schema:** Vehicles, Trip logs, PID samples (with encrypted payload blob), DTC events, custom dashboards.
- **DataStore:** Region/unit preferences + toggles (HUD, encryption, cloud uploads). Observed as Flows for real-time UI updates.
- **Encryption:** Placeholder XOR via `LogEncryption` demonstrates the plumbing; swap with AES/GCM by replacing the helper (documented in roadmap).

## Developer ergonomics
- **Mocking:** `MockObdAdapter` emits realistic RPM/speed/coolant series. `TripLoggingController` can log mock VINs for replay.
- **Raw console:** Settings screen includes a raw AT/OBD command sender calling `ObdConnectionManager.sendRaw`.
- **Extensions:** Partner apps can observe `ObdConnectionManager.telemetry()` Flow or use the PID pack JSON schema for manufacturer-specific data.

## Testing & CI
- **Unit tests:** Expression evaluator, DTC decoder, command queue priority. Extend with DAO tests by adding Room in-memory database under `data/src/test`.
- **UI test:** Compose screenshot-level check for the dashboard screen.
- **CI:** `.github/workflows/ci.yml` runs `./gradlew lint ktlintCheck detekt test assembleDebug` and hooks for future connected tests.

## Acceptance walkthrough
1. **Demo 1** (`docs/demo/demo1.gif`): Adapter scan + `ATI` command echo.
2. **Demo 2** (`demo2.gif`): Dashboard speed/RPM live tiles with latency tray.
3. **Demo 3** (`demo3.gif`): DTC list + parked-only clear.
4. **Demo 4** (`demo4.gif`): Start logging, capture trip, replay entries.
5. **Demo 5** (`demo5.gif`): Import `docs/pids/base_pack.json`, view custom metric tile.

## Roadmap anchors
- Replace XOR with Android Keystore-backed AES.
- ISO-TP multi-frame assembler + manufacturer PID packs (Maruti, Tata, VW).
- HUD mirroring auto mode + color-blind palettes.
- Firebase Test Lab matrix + telemetry export service / plugin SDK.
