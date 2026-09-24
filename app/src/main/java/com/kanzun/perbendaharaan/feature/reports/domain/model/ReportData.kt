package com.kanzun.perbendaharaan.feature.reports.domain.model

data class ReportRow(
    val columns: List<String>,
)

data class ReportSummaryItem(
    val label: String,
    val value: String,
)

data class ReportContent(
    val reportType: ReportType,
    val periodLabel: String,
    val summaries: List<ReportSummaryItem> = emptyList(),
    val tableHeaders: List<String> = emptyList(),
    val tableRows: List<ReportRow> = emptyList(),
    val mosqueName: String = "Masjid Agung Kanzun",
    val mosqueAddress: String = "Jl. Raya Masjid No. 1, Jakarta",
    val mosquePhone: String = "0812-3456-7890",
    val mosqueEmail: String = "info@kanzunmasjid.org",
    val chairmanName: String = "H. Ahmad Dahlan",
    val treasurerName: String = "H. Muhammad Hatta",
    val logoPath: String = "",
)
