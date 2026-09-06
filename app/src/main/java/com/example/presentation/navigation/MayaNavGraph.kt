package com.example.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.presentation.about.AboutScreen
import com.example.presentation.advanced.AdvancedSettingsScreen
import com.example.presentation.advanced.AudioSettingsScreen
import com.example.presentation.advanced.EmergencySosScreen
import com.example.presentation.advanced.EventTriggersScreen
import com.example.presentation.advanced.ScreenLockScreen
import com.example.presentation.advanced.ThemeSettingsScreen
import com.example.presentation.advanced.TouchGuardScreen
import com.example.presentation.advanced.VoiceGuardianScreen
import com.example.presentation.chat.ChatScreen
import com.example.presentation.home.HomeScreen
import com.example.presentation.home.HomeViewModel
import com.example.presentation.journal.JournalScreen
import com.example.presentation.markets.MarketsScreen
import com.example.presentation.memories.MemoriesScreen
import com.example.presentation.permissions.PermissionsOnboardingScreen
import com.example.presentation.privacy.PrivacyPolicyScreen
import com.example.presentation.rules.MayaRulesScreen
import com.example.presentation.scan.ScanScreen
import com.example.presentation.settings.ConnectedAccountsScreen
import com.example.presentation.settings.EmailSettingsScreen
import com.example.presentation.settings.MayaAssistantSettingsScreen
import com.example.presentation.settings.PersonalSettingsScreen
import com.example.presentation.settings.SettingsScreen
import com.example.presentation.settings.SkillsScreen
import com.example.presentation.settings.SocialMediaSettingsScreen
import com.example.presentation.settings.SubAgentsScreen
import com.example.presentation.settings.VoiceSelectionScreen
import com.example.presentation.settings.WhatsAppSettingsScreen
import com.example.presentation.study.StudyWhiteboardScreen
import com.example.presentation.upgrade.UpgradeScreen

@Composable
fun MayaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable("scan") {
            ScanScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable("memories") {
            MemoriesScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable("chat") {
            ChatScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable("settings") {
            SettingsScreen(onNavigate = { route -> navController.navigate(route) })
        }

        // Settings Sub-screens
        composable("settings_personal") {
            PersonalSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("settings_voice") {
            VoiceSelectionScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("settings_assistant") {
            MayaAssistantSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("settings_skills") {
            SkillsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("settings_subagents") {
            SubAgentsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("settings_email") {
            EmailSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("settings_whatsapp") {
            WhatsAppSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("settings_social") {
            SocialMediaSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("settings_connected") {
            ConnectedAccountsScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Advanced Settings
        composable("settings_advanced") {
            AdvancedSettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable("adv_theme") {
            ThemeSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("adv_audio") {
            AudioSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("adv_voice_guardian") {
            VoiceGuardianScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("adv_emergency_sos") {
            EmergencySosScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("adv_touch_guard") {
            TouchGuardScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("adv_screen_lock") {
            ScreenLockScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("adv_triggers") {
            EventTriggersScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Features & Capabilities
        composable("rules") {
            MayaRulesScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("study") {
            StudyWhiteboardScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("journal") {
            JournalScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("markets") {
            MarketsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("permissions") {
            PermissionsOnboardingScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("upgrade") {
            UpgradeScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("about") {
            AboutScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("privacy") {
            PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
