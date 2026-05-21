package com.valentinesgarage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.valentinesgarage.ui.theme.ValentinesGarageTheme
import com.valentinesgarage.ui.navigation.GarageNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single Activity — the only entry point into the app.
 * All screens are Composable destinations managed by Navigation Compose.
 * Using single-activity architecture as per Android best practices.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValentinesGarageTheme {
                GarageNavHost()
            }
        }
    }
}
