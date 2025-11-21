package com.autoflow.obd.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.autoflow.obd.adapter.AdapterScanRoute
import com.autoflow.obd.features.dashboard.DashboardRoute
import com.autoflow.obd.features.diagnostics.DiagnosticsRoute
import com.autoflow.obd.features.logs.LogsRoute
import com.autoflow.obd.features.performance.PerformanceRoute
import com.autoflow.obd.onboarding.OnboardingScreen
import com.autoflow.obd.settings.SettingsRoute
import com.autoflow.obd.vehicle.VehicleProfileRoute

object Destinations {
    const val Onboarding = "onboarding"
    const val Vehicle = "vehicle"
    const val Adapter = "adapter"
    const val Dashboard = "dashboard"
    const val Diagnostics = "diagnostics"
    const val Logs = "logs"
    const val Performance = "performance"
    const val Settings = "settings"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destinations.Onboarding) {
        composable(Destinations.Onboarding) {
            OnboardingScreen(onContinue = {
                navController.navigate(Destinations.Vehicle)
            })
        }
        composable(Destinations.Vehicle) {
            VehicleProfileRoute(onNext = {
                navController.navigate(Destinations.Adapter)
            })
        }
        composable(Destinations.Adapter) {
            AdapterScanRoute(onAdapterSelected = {
                navController.navigate(Destinations.Dashboard) {
                    popUpTo(Destinations.Onboarding) { inclusive = true }
                }
            })
        }
        composable(Destinations.Dashboard) { DashboardRoute() }
        composable(Destinations.Diagnostics) { DiagnosticsRoute() }
        composable(Destinations.Logs) { LogsRoute() }
        composable(Destinations.Performance) { PerformanceRoute() }
        composable(Destinations.Settings) { SettingsRoute() }
    }
}
