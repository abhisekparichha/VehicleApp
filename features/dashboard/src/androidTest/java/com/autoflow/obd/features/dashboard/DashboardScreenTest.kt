package com.autoflow.obd.features.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.autoflow.obd.core.model.TelemetryStatus
import com.autoflow.obd.core.model.TelemetryValue
import com.autoflow.obd.ui.theme.DriveTheme
import kotlinx.datetime.Clock
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun renders_speed_widget() {
        composeRule.setContent {
            DriveTheme {
                DashboardScreen(
                    state = DashboardUiState(
                        speed = TelemetryValue("0D", "01", "Speed", 42.0, "km/h", TelemetryStatus.NORMAL, Clock.System.now()),
                        rpm = TelemetryValue("0C", "01", "RPM", 1800.0, "rpm", TelemetryStatus.NORMAL, Clock.System.now())
                    )
                )
            }
        }

        composeRule.onNodeWithText("km/h").assertIsDisplayed()
    }
}
