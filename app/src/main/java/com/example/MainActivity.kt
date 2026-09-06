package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.AssistantRepository
import com.example.presentation.components.MayaBottomBar
import com.example.presentation.components.MayaDrawerContent
import com.example.presentation.components.MayaTopBar
import com.example.presentation.navigation.MayaNavGraph
import com.example.presentation.security.AppLockScreen
import com.example.service.BackgroundAudioService
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        try {
            BackgroundAudioService.startService(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestPermissionsIfNeeded()

        setContent {
            MayaTheme {
                val repository = remember { AssistantRepository.getInstance(applicationContext) }
                val settings by repository.settings.collectAsState()
                val assistantState by repository.assistantState.collectAsState()
                val connectionState by repository.connectionState.collectAsState()
                val micAmplitude by repository.micAmplitude.collectAsState()

                var isAppUnlocked by remember(settings.appLockEnabled) {
                    androidx.compose.runtime.mutableStateOf(
                        !settings.appLockEnabled || (settings.phonePattern.isBlank() && settings.phonePin.isBlank())
                    )
                }

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "home"

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val primaryBottomRoutes = setOf("home", "scan", "memories", "chat", "settings")
                val showBottomBar = currentRoute in primaryBottomRoutes

                Box(modifier = Modifier.fillMaxSize()) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = drawerState.isOpen,
                        drawerContent = {
                            MayaDrawerContent(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    scope.launch { drawerState.close() }
                                    if (route != currentRoute) {
                                        navController.navigate(route) {
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                onCloseDrawer = {
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    ) {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MayaNavyBackground),
                            containerColor = MayaNavyBackground,
                            topBar = {
                                if (showBottomBar) {
                                    MayaTopBar(
                                        title = "Maya",
                                        subtitle = if (connectionState == com.example.core.model.ConnectionState.CONNECTED) "Live" else null,
                                        onMenuClick = {
                                            scope.launch {
                                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                            }
                                        },
                                        onAvatarClick = {
                                            navController.navigate("settings")
                                        },
                                        onNotificationClick = {
                                            navController.navigate("notifications")
                                        }
                                    )
                                }
                            },
                            bottomBar = {
                                if (showBottomBar) {
                                    MayaBottomBar(
                                        currentRoute = currentRoute,
                                        assistantState = assistantState,
                                        micAmplitude = micAmplitude,
                                        onNavigate = { route ->
                                            if (route != currentRoute) {
                                                navController.navigate(route) {
                                                    popUpTo("home") { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        onMicClick = {
                                            repository.toggleVoiceAssistant()
                                        }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                                    .background(MayaNavyBackground)
                            ) {
                                MayaNavGraph(
                                    navController = navController,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    // Security App Lock Screen if enabled
                    if (!isAppUnlocked && settings.appLockEnabled && (settings.phonePattern.isNotBlank() || settings.phonePin.isNotBlank())) {
                        AppLockScreen(
                            settings = settings,
                            onUnlocked = { isAppUnlocked = true }
                        )
                    }
                }
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val neededPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (neededPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(neededPermissions.toTypedArray())
        } else {
            try {
                BackgroundAudioService.startService(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

