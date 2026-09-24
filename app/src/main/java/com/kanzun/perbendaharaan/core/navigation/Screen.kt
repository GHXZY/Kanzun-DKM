package com.kanzun.perbendaharaan.core.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Treasurer : Screen("treasurer")
    data object Reports : Screen("reports")
    data object Settings : Screen("settings")
    data object CashFlow : Screen("cash_flow")
    data object Assets : Screen("assets")
    data object Zakat : Screen("zakat")
    data object Fundraising : Screen("fundraising")
    data object Budget : Screen("budget")
    data object Audit : Screen("audit")
    data object Notifications : Screen("notifications")
    data object Preview : Screen("preview")
    data object About : Screen("about")
    data object PdfPreview : Screen("pdf_preview")
}
