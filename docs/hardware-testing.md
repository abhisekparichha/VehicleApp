# Hardware Testing & Safety Checklist

1. **Preparation**
   - Confirm the vehicle is on private property for actuator tests.
   - Inspect the OBD port for debris; avoid forcing the adapter.
   - Keep a fire extinguisher and wheel chocks available for dyno/0–60 runs.
2. **Adapter validation**
   - With ignition ON/engine OFF, plug in adapter and run `ATI`, `ATZ`, `ATSP0` via the in-app raw command console.
   - Verify voltage is between 11.5–14.8 V; below this abort tests.
3. **Protocol detection**
   - Allow the health check to auto-detect ISO9141/KWP/J1850/CAN. Record the detected protocol and ECU addresses.
4. **Driving tests**
   - Use a passenger to operate the app where possible.
   - Log trips only after brake pedal check + seat belts fastened.
   - For 0–60/dyno, choose a flat, empty stretch and abort above 80 % throttle if instability is detected.
5. **Vehicle matrix**
   - Petrol: Maruti Baleno/Ciaz, Hyundai i20, Honda City.
   - Diesel: Tata Nexon, Mahindra XUV700, Toyota Innova.
   - International: VW Golf, Ford F-150 (CAN), BMW 3-series.
6. **Exit criteria**
   - After testing, unplug adapter, check for stored DTCs, and clear only when parked.
   - Export logs and attach protocol + firmware info to the matrix spreadsheet.
