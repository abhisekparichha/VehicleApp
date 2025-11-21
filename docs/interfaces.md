## Interfaces & Contracts

### Connectivity & Adapters
- `ObdAdapter`
  - Properties: `endpoint: AdapterEndpoint`
  - Functions: `connect()`, `disconnect()`, `isConnected()`, `sendCommand(String)`, `frames(): Flow<ObdFrame>`, `healthCheck(): AdapterStatus`
- `AdapterEndpoint`
  - Implementations: `BluetoothEndpoint`, `BleEndpoint`, `WifiEndpoint`, `MockEndpoint`
- `ObdAdapterFactory`
  - `fun create(endpoint: AdapterEndpoint): ObdAdapter`

### Command & Protocol
- `CommandQueue`
  - `enqueue(ObdCommand)`
  - `drainNext(): ObdCommand?`
  - `onEnqueued(): SharedFlow<Unit>`
- `ObdCommand`
  - Fields: `mode`, `pid`, `priority`, `payload`, `createdAt`
- `PidRegistry`
  - `definitions(): StateFlow<Map<String, PidDefinition>>`
  - `find(mode, pid)`
  - `importPack(rawJson)`
- `ExpressionEvaluator`
  - `evaluate(expression: String, bytes: ByteArray): Double`
- `ObdResponseParser`
  - `parse(frame: String, registry: PidRegistry): TelemetryValue?`
- `DtcDecoder`
  - `decode(frame: String): List<String>`

### Connection Management
- `ObdConnectionManager`
  - `scanAdapters()`
  - `connect(endpoint: AdapterEndpoint)`
  - `disconnect()`
  - `sendRaw(command: String)`
  - Observables: `telemetry(): Flow<TelemetryValue>`, `state(): StateFlow<ConnectionState>`, `adapters(): StateFlow<List<AdapterSummary>>`, `dtcStream(): Flow<List<String>>`
  - Helpers: `endpointFromSummary(summary: AdapterSummary)`
- `ConnectionState` sealed interface (`Disconnected`, `Connecting`, `Connected`)

### Safety & Preferences
- `DrivingStateMonitor`
  - `observe(): Flow<DrivingState>`
  - `updateFromSpeed(kph: Double)`
- `ParkedActionGate`
  - `canPerformDestructiveActions(): Flow<Boolean>`
- `UserPreferencesRepository`
  - `preferences(): Flow<UnitPreferences>`
  - `update(block: UnitPreferences.() -> UnitPreferences)`

### Data Layer Repositories
- `VehicleRepository`
  - `vehicles(): Flow<List<VehicleEntity>>`
  - `save(vehicle)`
- `TripLogRepository`
  - `startTrip(vin, startTime)`
  - `endTrip(tripId, endTime)`
  - `recordSamples(tripId, samples)`
  - `trips(): Flow<List<TripLogEntity>>`
- `DiagnosticsRepository`
  - `observeDtc(vin)`
  - `recordDtc(event)`
- `DashboardRepository`
  - `dashboards(): Flow<List<DashboardLayoutEntity>>`
  - `save(layoutJson, name)`

### Use Cases
- `ImportCustomPidUseCase.execute(rawJson)`
- `DashboardTelemetryUseCase`
  - `observeSpeed()`, `observeRpm()`, `observeAll()`
- `TripLoggingController`
  - `start(vin)`
  - `stop()`

### Feature ViewModels (selected APIs)
- `DashboardViewModel.state: StateFlow<DashboardUiState>`
  - `requestMockConnection()`
- `DiagnosticsViewModel.state`
  - `readCodes()`, `clearCodes()`
- `LogsViewModel.state`
  - `toggleLogging()`
- `PerformanceViewModel.state`
  - `startZeroToSixty()`, `reset()`
- `SettingsViewModel.preferences`
  - `setUnit`, `toggleNightMode`, `toggleHud`, `toggleEncryption`, `toggleCloud`, `sendRawCommand`
- `VehicleProfileViewModel.saveProfile(...)`
- `AdapterScanViewModel.adapters`
  - `connect(summary)`

### UI Components
- `SpeedWidget(speedValue, unit, status, modifier)`
- `RpmGauge(rpm, maxRpm, modifier)`
- `MetricCard(label, value, unit, modifier)`
- `Sparkline(values, modifier)`

### Navigation Destinations
- `Destinations` constants: `Onboarding`, `Vehicle`, `Adapter`, `Dashboard`, `Diagnostics`, `Logs`, `Performance`, `Settings`
- `AppNavGraph(navController)` wires composable routes.
