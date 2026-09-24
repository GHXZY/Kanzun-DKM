package com.kanzun.perbendaharaan.core.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kanzun.perbendaharaan.core.designsystem.KanzunTheme
import com.kanzun.perbendaharaan.core.designsystem.components.AppTopBar
import com.kanzun.perbendaharaan.core.designsystem.components.ContextualFeatureHeader
import com.kanzun.perbendaharaan.core.designsystem.components.FloatingNavBar
import com.kanzun.perbendaharaan.core.designsystem.components.IsolatedFeatureTopBar
import com.kanzun.perbendaharaan.core.navigation.KanzunNavHost
import com.kanzun.perbendaharaan.core.navigation.Screen
import com.kanzun.perbendaharaan.feature.notifications.presentation.NotificationViewModel
import kotlinx.coroutines.launch

@Composable
fun AppShell(
    notificationViewModel: NotificationViewModel = hiltViewModel(),
) {
    var isDarkTheme by remember { mutableStateOf(false) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "main_pager"

    val unreadCount by notificationViewModel.unreadCountState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })

    val mainRoutes = setOf(
        Screen.Dashboard.route,
        Screen.Treasurer.route,
        Screen.Reports.route,
        Screen.Settings.route,
        "main_pager",
    )

    val isolatedFeatureRoutes = setOf(
        Screen.CashFlow.route,
        Screen.Assets.route,
        Screen.Zakat.route,
        Screen.Fundraising.route,
        Screen.Budget.route,
        Screen.About.route,
    )

    val isMainScreen = currentRoute in mainRoutes || currentRoute == "main_pager"
    val isIsolatedScreen = currentRoute in isolatedFeatureRoutes

    val activeMainRoute = when (pagerState.currentPage) {
        0 -> Screen.Dashboard.route
        1 -> Screen.Treasurer.route
        2 -> Screen.Reports.route
        3 -> Screen.Settings.route
        else -> Screen.Dashboard.route
    }

    KanzunTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                if (isMainScreen) {
                    AppTopBar(
                        mosqueName = "Masjid Agung Al-Mubarak",
                        mosqueAddress = "Jl. Ahmad Yani No. 45, Jakarta",
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = !isDarkTheme },
                        unreadCount = unreadCount,
                        onNotificationClick = {
                            navController.navigate(Screen.Notifications.route)
                        },
                    )
                } else if (isIsolatedScreen) {
                    val isolatedTitle = when (currentRoute) {
                        Screen.CashFlow.route -> "Arus Kas"
                        Screen.Assets.route -> "Aset Masjid"
                        Screen.Zakat.route -> "Zakat"
                        Screen.Fundraising.route -> "Target Dana"
                        Screen.Budget.route -> "RAPBM"
                        Screen.About.route -> "Tentang Aplikasi"
                        else -> ""
                    }
                    IsolatedFeatureTopBar(
                        title = isolatedTitle,
                        onBackClick = { navController.popBackStack() },
                    )
                } else {
                    val featureTitle = when (currentRoute) {
                        Screen.Audit.route -> "Audit Trail"
                        Screen.Notifications.route -> "Notifikasi"
                        "preview" -> "Galeri Komponen UI"
                        else -> "Kanzun"
                    }

                    ContextualFeatureHeader(
                        title = featureTitle,
                        onBackClick = { navController.popBackStack() },
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                KanzunNavHost(
                    navController = navController,
                    pagerState = pagerState,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme },
                )

                if (isMainScreen) {
                    FloatingNavBar(
                        currentRoute = activeMainRoute,
                        onItemSelected = { route ->
                            val targetIndex = when (route) {
                                Screen.Dashboard.route -> 0
                                Screen.Treasurer.route -> 1
                                Screen.Reports.route -> 2
                                Screen.Settings.route -> 3
                                else -> 0
                            }
                            if (pagerState.currentPage != targetIndex) {
                                scope.launch {
                                    pagerState.animateScrollToPage(targetIndex)
                                }
                            }
                            if (currentRoute != "main_pager" && currentRoute !in mainRoutes) {
                                navController.navigate("main_pager") {
                                    popUpTo("main_pager") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
        }
    }
}
