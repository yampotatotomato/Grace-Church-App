package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.ChurchTab
import com.example.ui.ChurchViewModel
import com.example.ui.screens.*

sealed class ChurchScreen(val route: String, val tab: ChurchTab?) {
    object Home : ChurchScreen("screen_home", ChurchTab.HOME)
    object Scripture : ChurchScreen("screen_scripture", ChurchTab.SCRIPTURE)
    object Devotion : ChurchScreen("screen_devotion", ChurchTab.DEVOTION)
    object Profile : ChurchScreen("screen_profile", ChurchTab.PROFILE)
    object Journal : ChurchScreen("screen_journal", ChurchTab.JOURNAL)
    object Sermons : ChurchScreen("screen_sermons", ChurchTab.SERMONS)
    object Community : ChurchScreen("screen_community", ChurchTab.COMMUNITY)
    object CompanionPortal : ChurchScreen("screen_companion", ChurchTab.COMPANION)
    object Settings : ChurchScreen("screen_settings", null)

    companion object {
        fun fromTab(tab: ChurchTab): ChurchScreen {
            return when (tab) {
                ChurchTab.HOME -> Home
                ChurchTab.SCRIPTURE -> Scripture
                ChurchTab.DEVOTION -> Devotion
                ChurchTab.PROFILE -> Profile
                ChurchTab.JOURNAL -> Journal
                ChurchTab.SERMONS -> Sermons
                ChurchTab.COMMUNITY -> Community
                ChurchTab.COMPANION -> CompanionPortal
            }
        }

        fun fromRoute(route: String?): ChurchScreen {
            return when (route) {
                Home.route -> Home
                Scripture.route -> Scripture
                Devotion.route -> Devotion
                Profile.route -> Profile
                Journal.route -> Journal
                Sermons.route -> Sermons
                Community.route -> Community
                CompanionPortal.route -> CompanionPortal
                Settings.route -> Settings
                else -> Home
            }
        }
    }
}

/**
 * Compose Navigation Host managing screen switches between Scripture, Devotional, Profile, and all sanctuary hubs.
 */
@Composable
fun ChurchNavHost(
    navController: NavHostController,
    viewModel: ChurchViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ChurchScreen.Home.route,
        modifier = modifier
    ) {
        composable(ChurchScreen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateTab = { targetTab ->
                    navController.navigateToTab(targetTab)
                }
            )
        }

        composable(ChurchScreen.Scripture.route) {
            ScriptureScreen(
                viewModel = viewModel
            )
        }

        composable(ChurchScreen.Devotion.route) {
            DevotionScreen(
                viewModel = viewModel
            )
        }

        composable(ChurchScreen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onNavigateToScripture = {
                    navController.navigateToTab(ChurchTab.SCRIPTURE)
                },
                onNavigateToDevotional = {
                    navController.navigateToTab(ChurchTab.DEVOTION)
                },
                onNavigateToSettings = {
                    viewModel.openNotificationSettings()
                },
                onNavigateTab = { targetTab ->
                    navController.navigateToTab(targetTab)
                }
            )
        }

        composable(ChurchScreen.Journal.route) {
            JournalScreen(
                viewModel = viewModel
            )
        }

        composable(ChurchScreen.Sermons.route) {
            PastorsScreen(
                viewModel = viewModel
            )
        }

        composable(ChurchScreen.Community.route) {
            PrayerGroupsScreen(
                viewModel = viewModel
            )
        }

        composable(ChurchScreen.CompanionPortal.route) {
            CompanionScreen(
                viewModel = viewModel,
                onNavigateTab = { targetTab ->
                    navController.navigateToTab(targetTab)
                }
            )
        }

        composable(ChurchScreen.Settings.route) {
            SettingsNotificationsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

fun NavHostController.navigateToTab(tab: ChurchTab) {
    val destination = ChurchScreen.fromTab(tab)
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
