# Developer Guide

## Adding a custom PID programmatically
1. Create a JSON payload following `PidPack` schema (see `docs/pids/base_pack.json`).
2. Call `ImportCustomPidUseCase.execute(jsonString)` from a settings or developer screen.
3. The PID instantly becomes part of the registry and can be referenced by dashboards/loggers.

## Creating dashboard widgets
- Compose widgets reside in `ui/components/`. Extend them or add a new composable.
- Register the widget in `DashboardScreen` by adding tiles or hooking into the future drag-and-drop editor (`features/dashboard/editor`).
- Persist layouts via `DashboardRepository.save()` which stores JSON payloads.

## Extending transports
- Implement `ObdAdapter` for the new medium (e.g., USB-OTG serial) and wire it into `ObdAdapterFactory`.
- Provide DI bindings and add any specific permissions in `AndroidManifest.xml`.

## Plugin / data export
- Any module can collect `ObdConnectionManager.telemetry()` and `dtcStream()` flows.
- For IPC, wrap those flows into an AIDL/IntentService (planned in roadmap).

## Debugging tips
- Use the raw console in Settings to send AT/OBD commands (`ATI`, `ATH1`, `01 0C`).
- Enable Timber logs (debug builds) to inspect adapter IO.
- Mock adapter can be forced via `DashboardViewModel.requestMockConnection()` for UI-only testing.
