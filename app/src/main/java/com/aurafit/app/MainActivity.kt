package com.aurafit.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aurafit.app.core.security.EncryptedPrefs
import com.aurafit.app.core.theme.AuraFitTheme
import com.aurafit.app.core.theme.ObsidianDark
import com.aurafit.app.presentation.camera.FoodCameraScreen
import com.aurafit.app.presentation.dashboard.DashboardScreen
import com.aurafit.app.presentation.dashboard.DashboardViewModel
import com.aurafit.app.presentation.macro.MacroTrackerScreen
import com.aurafit.app.presentation.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var encryptedPrefs: EncryptedPrefs

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission state resolved
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request Camera permission if not yet granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            AuraFitTheme {
                val navController = rememberNavController()
                val dashboardViewModel: DashboardViewModel = hiltViewModel()
                var activeDestination by remember { mutableStateOf("dashboard") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = ObsidianDark,
                            tonalElevation = 0.dp,
                            modifier = Modifier.height(72.dp)
                        ) {
                            val tabs = listOf(
                                Triple("dashboard", "📊", "Dashboard"),
                                Triple("camera", "📸", "Camera"),
                                Triple("macros", "🥗", "Log Food"),
                                Triple("settings", "⚙️", "Settings")
                            )
                            tabs.forEach { (route, icon, label) ->
                                NavigationBarItem(
                                    selected = activeDestination == route,
                                    onClick = {
                                        activeDestination = route
                                        navController.navigate(route) {
                                            popUpTo("dashboard") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Text(icon, fontSize = 20.sp) },
                                    label = { Text(label, fontSize = 9.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = Color.Gray,
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("dashboard") {
                            activeDestination = "dashboard"
                            DashboardScreen(
                                viewModel = dashboardViewModel,
                                onNavigateToCamera = {
                                    activeDestination = "camera"
                                    navController.navigate("camera") {
                                        popUpTo("dashboard") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onNavigateToTextLog = {
                                    activeDestination = "macros"
                                    navController.navigate("macros") {
                                        popUpTo("dashboard") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }

                        composable("camera") {
                            activeDestination = "camera"
                            FoodCameraScreen(
                                dashboardViewModel = dashboardViewModel,
                                onNavigateToDashboard = {
                                    activeDestination = "dashboard"
                                    navController.navigate("dashboard") {
                                        popUpTo("dashboard") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }

                        composable("macros") {
                            activeDestination = "macros"
                            MacroTrackerScreen(dashboardViewModel = dashboardViewModel)
                        }

                        composable("settings") {
                            activeDestination = "settings"
                            SettingsScreen(encryptedPrefs = encryptedPrefs)
                        }
                    }
                }
            }
        }
    }
}
