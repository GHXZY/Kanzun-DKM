package com.kanzun.perbendaharaan.core.navigation

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kanzun.perbendaharaan.feature.assets.presentation.AssetsScreen
import com.kanzun.perbendaharaan.feature.budget.presentation.BudgetScreen
import com.kanzun.perbendaharaan.feature.dashboard.presentation.DashboardScreen
import com.kanzun.perbendaharaan.feature.financial.presentation.CashFlowScreen
import com.kanzun.perbendaharaan.feature.financial.presentation.TreasurerScreen
import com.kanzun.perbendaharaan.feature.fundraising.presentation.FundraisingScreen
import com.kanzun.perbendaharaan.feature.notifications.presentation.NotificationScreen
import com.kanzun.perbendaharaan.feature.preview.presentation.DesignSystemPreviewScreen
import com.kanzun.perbendaharaan.feature.reports.presentation.PdfPreviewScreen
import com.kanzun.perbendaharaan.feature.reports.presentation.ReportsScreen
import com.kanzun.perbendaharaan.feature.settings.presentation.AboutScreen
import com.kanzun.perbendaharaan.feature.settings.presentation.SettingsScreen
import com.kanzun.perbendaharaan.feature.zakat.presentation.ZakatScreen

@Composable
fun KanzunNavHost(
    navController: NavHostController,
    pagerState: PagerState,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = "main_pager",
        modifier = modifier,
    ) {
        composable("main_pager") {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier,
            ) { page ->
                when (page) {
                    0 -> DashboardScreen(
                        onNavigateToFeature = { route -> navController.navigate(route) },
                    )
                    1 -> TreasurerScreen(
                        onNavigateToFeature = { route -> navController.navigate(route) },
                    )
                    2 -> ReportsScreen(
                        onNavigateToPdfPreview = { navController.navigate(Screen.PdfPreview.route) },
                    )
                    3 -> SettingsScreen(
                        onNavigateToAbout = { navController.navigate(Screen.About.route) },
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleTheme,
                        onNavigateToPreview = { navController.navigate(Screen.Preview.route) },
                    )
                }
            }
        }
        composable(Screen.Dashboard.route) {
            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> DashboardScreen(onNavigateToFeature = { navController.navigate(it) })
                    1 -> TreasurerScreen(onNavigateToFeature = { navController.navigate(it) })
                    2 -> ReportsScreen(onNavigateToPdfPreview = { navController.navigate(Screen.PdfPreview.route) })
                    3 -> SettingsScreen(onNavigateToAbout = { navController.navigate(Screen.About.route) }, isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme, onNavigateToPreview = { navController.navigate(Screen.Preview.route) })
                }
            }
        }
        composable(Screen.Treasurer.route) {
            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> DashboardScreen(onNavigateToFeature = { navController.navigate(it) })
                    1 -> TreasurerScreen(onNavigateToFeature = { navController.navigate(it) })
                    2 -> ReportsScreen(onNavigateToPdfPreview = { navController.navigate(Screen.PdfPreview.route) })
                    3 -> SettingsScreen(onNavigateToAbout = { navController.navigate(Screen.About.route) }, isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme, onNavigateToPreview = { navController.navigate(Screen.Preview.route) })
                }
            }
        }
        composable(Screen.Reports.route) {
            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> DashboardScreen(onNavigateToFeature = { navController.navigate(it) })
                    1 -> TreasurerScreen(onNavigateToFeature = { navController.navigate(it) })
                    2 -> ReportsScreen(onNavigateToPdfPreview = { navController.navigate(Screen.PdfPreview.route) })
                    3 -> SettingsScreen(onNavigateToAbout = { navController.navigate(Screen.About.route) }, isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme, onNavigateToPreview = { navController.navigate(Screen.Preview.route) })
                }
            }
        }
        composable(Screen.Settings.route) {
            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> DashboardScreen(onNavigateToFeature = { navController.navigate(it) })
                    1 -> TreasurerScreen(onNavigateToFeature = { navController.navigate(it) })
                    2 -> ReportsScreen(onNavigateToPdfPreview = { navController.navigate(Screen.PdfPreview.route) })
                    3 -> SettingsScreen(onNavigateToAbout = { navController.navigate(Screen.About.route) }, isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme, onNavigateToPreview = { navController.navigate(Screen.Preview.route) })
                }
            }
        }
        composable(Screen.CashFlow.route) {
            CashFlowScreen()
        }
        composable(Screen.Assets.route) {
            AssetsScreen()
        }
        composable(Screen.Zakat.route) {
            ZakatScreen()
        }
        composable(Screen.Fundraising.route) {
            FundraisingScreen()
        }
        composable(Screen.Budget.route) {
            BudgetScreen()
        }
        composable(Screen.Notifications.route) {
            NotificationScreen(
                onNavigateToFeature = { route -> navController.navigate(route) },
            )
        }
        composable(Screen.Preview.route) {
            DesignSystemPreviewScreen()
        }
        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
        composable(Screen.PdfPreview.route) {
            PdfPreviewScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
