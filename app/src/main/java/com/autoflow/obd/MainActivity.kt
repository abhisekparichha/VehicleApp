package com.autoflow.obd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import com.autoflow.obd.navigation.AppNavGraph
import com.autoflow.obd.navigation.Destinations
import com.autoflow.obd.ui.theme.DriveTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DriveTheme {
                DriveApp()
            }
        }
    }
}

@Composable
fun DriveApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomDestinations = listOf(
        Destinations.Dashboard,
        Destinations.Diagnostics,
        Destinations.Logs,
        Destinations.Performance,
        Destinations.Settings
    )
    Scaffold(
        bottomBar = {
            if (currentRoute in bottomDestinations) {
                NavigationBar {
                    bottomDestinations.forEach { route ->
                        NavigationBarItem(
                            selected = currentRoute == route,
                            onClick = {
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = { Text(text = route.take(3).uppercase()) },
                            label = { Text(text = route.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AppNavGraph(navController = navController)
        }
    }
}
