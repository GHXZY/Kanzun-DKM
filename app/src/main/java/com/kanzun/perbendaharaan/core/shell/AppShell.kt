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
import android.content.Context
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kanzun.perbendaharaan.core.designsystem.components.SupportDonationDialog
import com.kanzun.perbendaharaan.feature.notifications.presentation.NotificationViewModel
import com.kanzun.perbendaharaan.feature.settings.presentation.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun AppShell(
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isDarkTheme by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }

    fun checkAndTriggerSupportDialog() {
        val prefs = context.getSharedPreferences("kanzun_support_prefs", Context.MODE_PRIVATE)
        val lastShownTime = prefs.getLong("last_support_dialog_timestamp", 0L)
        val currentTime = System.currentTimeMillis()
        val twoHoursInMillis = 2 * 60 * 60 * 1000L // 2 jam

        if (lastShownTime == 0L || (currentTime - lastShownTime >= twoHoursInMillis)) {
            showSupportDialog = true
            prefs.edit().putLong("last_support_dialog_timestamp", currentTime).apply()
        }
    }

    LaunchedEffect(Unit) {
        checkAndTriggerSupportDialog()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkAndTriggerSupportDialog()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "main_pager"

    val unreadCount by notificationViewModel.unreadCountState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
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
        Screen.PdfPreview.route,
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
                        mosqueName = settingsState.mosqueProfile?.name?.ifBlank { "Masjid Agung Al-Mubarak" } ?: "Masjid Agung Al-Mubarak",
                        mosqueAddress = settingsState.mosqueProfile?.address?.ifBlank { "Jl. Ahmad Yani No. 45, Jakarta" } ?: "Jl. Ahmad Yani No. 45, Jakarta",
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
                        Screen.PdfPreview.route -> "Preview Surat Laporan"
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

        if (showSupportDialog) {
            SupportDonationDialog(
                onDismiss = { showSupportDialog = false },
            )
        }
    }
}
