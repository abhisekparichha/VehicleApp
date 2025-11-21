## Component Architecture

### Modules Overview
- `app`: Android entry point, navigation graph (`AppNavGraph`), Hilt application class, onboarding + services.
- `core`: Cross-cutting concerns such as dispatchers, locale/unit preferences, safety gating, connectivity metadata, and lightweight encryption.
- `data`: Persistence layer (Room entities, DAO, repositories) plus DataStore-backed preference repository.
- `obd`: OBD stack (adapters, command queue, protocol decoders, PID registry, telemetry/logging use cases).
- `ui`: Compose theme + reusable widgets (SpeedWidget, RpmGauge, MetricCard, Sparkline).
- `features/*`: Vertical feature slices (`dashboard`, `diagnostics`, `logs`, `performance`) each with a ViewModel + Compose screen.

### App Layer
- `DriveSafeObdApp`: Hilt application, Timber initialization.
- `MainActivity`: Hosts `DriveApp` Scaffold with bottom navigation and `AppNavGraph`.
- Navigation destinations: Onboarding → Vehicle profile → Adapter scan → bottom tabs (Dashboard, Diagnostics, Logs, Performance, Settings).
- Foreground service (`ObdForegroundService`) keeps polling alive with persistent notification.

### Core Layer
- `DispatcherProvider`, `DefaultDispatcherProvider`: central coroutine context binding.
- `DrivingStateMonitor` + `ParkedActionGate`: evaluate speed telemetry to restrict destructive actions.
- `UnitPreferences`, `UnitSystem`: localization configuration shared across UI.
- `AdapterKind` / `AdapterSummary`: device discovery metadata.
- `LogEncryption`: placeholder XOR-based encryption for log payloads.

### Data Layer
- Room entities: `VehicleEntity`, `TripLogEntity`, `PidSampleEntity`, `DtcEventEntity`, `DashboardLayoutEntity`.
- DAO: `DriveSafeDao` exposes flows for vehicles, trips, DTCs, dashboards.
- Repositories: `VehicleRepository`, `TripLogRepository`, `DiagnosticsRepository`, `DashboardRepository`.
- `UserPreferencesRepository`: wraps DataStore for units, HUD, encryption, cloud toggles.

### OBD Layer
- Adapters: `BluetoothObdAdapter`, `BleObdAdapter`, `WifiObdAdapter`, `MockObdAdapter` implement `ObdAdapter`.
- Factory: `ObdAdapterFactory` selects adapter given an `AdapterEndpoint`.
- `CommandQueue`: priority-based queue for OBD commands.
- Protocol: `PidRegistry`, `PidDefinition`, `PidPack`, `ExpressionEvaluator`, `ObdResponseParser`, `DtcDecoder`.
- Connection & Use Cases: `ObdConnectionManager` orchestrates scanning, connecting, telemetry flows, DTC stream, raw command sending; `ImportCustomPidUseCase`, `DashboardTelemetryUseCase`, `TripLoggingController`.

### UI/Feature Layer
- `SpeedWidget`, `RpmGauge`, `MetricCard`, `Sparkline`: reusable composables respecting semantics and HUD needs.
- Feature ViewModels leverage repositories/use cases:
  - Dashboard: combines telemetry + preferences, exposes HUD state and polling rate.
  - Diagnostics: listens to DTC stream + parked gate.
  - Logs: toggles TripLoggingController, shows Room-backed trips.
  - Performance: runs 0–60 timer based on speed telemetry.
- Settings includes runtime permission rationales and raw OBD console.

### Docs & Tooling
- README + IMPLEMENTATION + developer guide + hardware checklist, PID packs, demo GIFs.
- CI (`.github/workflows/ci.yml`) runs lint/style/tests/build.
