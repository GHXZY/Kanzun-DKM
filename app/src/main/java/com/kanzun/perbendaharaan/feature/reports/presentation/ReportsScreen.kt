package com.kanzun.perbendaharaan.feature.reports.presentation

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.AppTextField
import com.kanzun.perbendaharaan.core.designsystem.components.ChipStatusType
import com.kanzun.perbendaharaan.core.designsystem.components.EmptyState
import com.kanzun.perbendaharaan.core.designsystem.components.KpiCard
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.ResponsiveContentContainer
import com.kanzun.perbendaharaan.core.designsystem.components.SecondaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.designsystem.components.SegmentedControl
import com.kanzun.perbendaharaan.core.designsystem.components.StatusChip
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.dashboard.presentation.components.FundAllocationPieChart
import com.kanzun.perbendaharaan.feature.dashboard.presentation.components.RekeningVsCashChart
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportContent
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportType
import com.kanzun.perbendaharaan.feature.reports.presentation.components.CashFlowAreaChart
import com.kanzun.perbendaharaan.core.util.formatAccountName
import com.kanzun.perbendaharaan.core.util.formatCategoryName
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateToPdfPreview: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val presets = listOf("7 Hari", "30 Hari", "Bulan Ini", "Bulan Lalu", "Tahun Ini")

    val totalIncomeCents = remember(uiState.filteredTransactions) {
        uiState.filteredTransactions
            .filter { it.type == TransactionType.INCOME && !it.isReversed }
            .sumOf { it.amountInCents }
    }
    val totalExpenseCents = remember(uiState.filteredTransactions) {
        uiState.filteredTransactions
            .filter { it.type == TransactionType.EXPENSE && !it.isReversed }
            .sumOf { it.amountInCents }
    }
    val netCashFlowCents = totalIncomeCents - totalExpenseCents

    val segmentFilteredTransactions = remember(
        uiState.filteredTransactions,
        uiState.transactionSegment,
        uiState.transactionSearchQuery,
    ) {
        val segmentTxs = when (uiState.transactionSegment) {
            1 -> uiState.filteredTransactions.filter { it.type == TransactionType.INCOME }
            2 -> uiState.filteredTransactions.filter { it.type == TransactionType.EXPENSE }
            else -> uiState.filteredTransactions
        }
        if (uiState.transactionSearchQuery.isBlank()) {
            segmentTxs
        } else {
            val query = uiState.transactionSearchQuery.trim()
            segmentTxs.filter { tx ->
                tx.title.contains(query, ignoreCase = true) ||
                    tx.categoryId.formatCategoryName().contains(query, ignoreCase = true) ||
                    tx.accountId.formatAccountName().contains(query, ignoreCase = true) ||
                    tx.categoryId.contains(query, ignoreCase = true) ||
                    tx.accountId.contains(query, ignoreCase = true)
            }
        }
    }

    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")) }
    val groupedTransactions = remember(segmentFilteredTransactions) {
        segmentFilteredTransactions.groupBy { tx ->
            dateFormat.format(Date(tx.timestamp))
        }
    }

    LaunchedEffect(uiState.generatedPdfFile) {
        val pdf = uiState.generatedPdfFile
        if (pdf != null) {
            Toast.makeText(context, "Dokumen PDF Berhasil Dibuat: ${pdf.name}", Toast.LENGTH_LONG).show()
            viewModel.clearPdfFile()
        }
    }

    ResponsiveContentContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = Spacing.MD)
                .padding(bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.LG),
        ) {
            Spacer(modifier = Modifier.height(Spacing.XS))

            // 1. TOP APP BAR (Compact Header)
            SectionHeader(
                title = "Laporan",
            )

            // 2. PERIOD FILTER (Presets)
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                presets.forEachIndexed { index, title ->
                    SegmentedButton(
                        selected = uiState.selectedPresetIndex == index,
                        onClick = { viewModel.applyPeriodPreset(index) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = presets.size),
                    ) {
                        Text(title, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // 3. FINANCIAL SUMMARY (3-Card Metrics Row)
            SectionHeader(
                title = "Ringkasan Keuangan",
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
            ) {
                KpiCard(
                    title = "Pemasukan",
                    valueText = Money.of(totalIncomeCents).formatRupiah(),
                    icon = Icons.Default.ArrowUpward,
                    iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                )
                KpiCard(
                    title = "Pengeluaran",
                    valueText = Money.of(totalExpenseCents).formatRupiah(),
                    icon = Icons.Default.ArrowDownward,
                    iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                    iconColor = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f),
                )
            }

            KpiCard(
                title = "Arus Kas Bersih",
                valueText = Money.of(netCashFlowCents).formatRupiah(),
                icon = Icons.Default.SwapHoriz,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth(),
            )

            // 4. CASH FLOW AREA CHART
            SectionHeader(
                title = "Arus Kas",
            )

            CashFlowAreaChart(
                dataPoints = uiState.cashFlowDataPoints,
            )

            // 5. REKENING VS CASH (Donut Chart)
            SectionHeader(
                title = "Rekening & Cash",
            )

            RekeningVsCashChart(
                breakdown = uiState.cashBreakdown,
                onCardClick = {},
            )

            // 6. ALOKASI DANA (Pie/Donut Chart)
            SectionHeader(
                title = "Alokasi Dana",
            )

            FundAllocationPieChart(
                allocation = uiState.fundAllocation,
                onCardClick = {},
            )

            // 7. STATUS KEUANGAN
            SectionHeader(
                title = "Status Keuangan",
            )

            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                    DetailRow("Pemasukan Periode Ini", Money.of(totalIncomeCents).formatRupiah())
                    DetailRow("Pengeluaran Periode Ini", Money.of(totalExpenseCents).formatRupiah())
                    DetailRow("Arus Kas Bersih", Money.of(netCashFlowCents).formatRupiah())

                    Spacer(modifier = Modifier.height(Spacing.XS))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Kondisi Keuangan Kas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (netCashFlowCents >= 0) {
                            StatusChip(
                                text = "Kondisi Keuangan Sehat",
                                type = ChipStatusType.SUCCESS,
                            )
                        } else {
                            StatusChip(
                                text = "Defisit Kas Periode Ini",
                                type = ChipStatusType.WARNING,
                            )
                        }
                    }
                }
            }

            // 8. RIWAYAT TRANSAKSI
            SectionHeader(
                title = "Riwayat Transaksi",
            )

            AppTextField(
                value = uiState.transactionSearchQuery,
                onValueChange = { viewModel.onTransactionSearchQueryChanged(it) },
                placeholder = { Text("Cari transaksi, kategori, atau rekening...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari Transaksi",
                    )
                },
                trailingIcon = if (uiState.transactionSearchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { viewModel.onTransactionSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus Pencarian",
                            )
                        }
                    }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            SegmentedControl(
                options = listOf("Semua", "Pemasukan", "Pengeluaran"),
                selectedIndex = uiState.transactionSegment,
                onOptionSelected = { viewModel.setTransactionSegment(it) },
            )

            if (segmentFilteredTransactions.isEmpty()) {
                EmptyState(
                    title = "Belum Ada Transaksi",
                    description = "Belum ada pencatatan kas pada periode atau filter yang dipilih.",
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.MD)) {
                    for ((dateHeader, txs) in groupedTransactions) {
                        Text(
                            text = dateHeader,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = Spacing.XS),
                        )

                        for (tx in txs) {
                            TransactionReportRow(transaction = tx)
                        }
                    }
                }
            }

            // 9. AUDIT LAPORAN (Report Overview)
            SectionHeader(
                title = "Audit Laporan",
            )

            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                    DetailRow("Periode Laporan", presets.getOrNull(uiState.selectedPresetIndex) ?: "Bulan Ini")
                    DetailRow("Jumlah Transaksi", "${uiState.filteredTransactions.size} pencatatan")
                    DetailRow("Total Pemasukan", Money.of(totalIncomeCents).formatRupiah())
                    DetailRow("Total Pengeluaran", Money.of(totalExpenseCents).formatRupiah())
                    DetailRow("Status Kelengkapan Data", "\u2713 Lengkap (Auditable)")
                }
            }

            // 10. EXPORT REPORT ACTION BUTTON (AT THE VERY BOTTOM)
            PrimaryButton(
                text = if (uiState.isGeneratingPdf) "Membuat PDF..." else "Generate Surat Laporan (PDF)",
                icon = Icons.Default.PictureAsPdf,
                onClick = { viewModel.openExportBottomSheet() },
                fullWidth = true,
                modifier = Modifier.padding(top = Spacing.MD),
            )
        }
    }

    // 11. GENERATE SURAT LAPORAN MODAL BOTTOM SHEET
    if (uiState.isExportBottomSheetOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { viewModel.closeExportBottomSheet() },
            sheetState = sheetState,
            scrimColor = Color.Black.copy(alpha = 0.70f),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shape = KanzunShapes.BottomSheet,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.LG)
                    .padding(bottom = Spacing.XL),
                verticalArrangement = Arrangement.spacedBy(Spacing.MD),
            ) {
                Text(
                    text = "Generate Surat Laporan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Konfigurasi jenis laporan dan waktu periode laporan langsung dari popup ini.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                // 1. PILIH JENIS LAPORAN
                Text(
                    text = "Pilih Jenis Laporan Surat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS)) {
                    ReportType.values().forEach { type ->
                        val isSelected = uiState.pdfReportType == type
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setPdfReportType(type) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setPdfReportType(type) },
                            )
                            Spacer(modifier = Modifier.width(Spacing.SM))
                            Column {
                                Text(
                                    text = type.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = type.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.XS))

                // 2. PILIH WAKTU / PERIODE DARI POPUP
                Text(
                    text = "Pilih Periode Waktu Laporan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                SegmentedControl(
                    options = listOf("Pilihan Bulan", "Rentang Tanggal (A-B)", "Preset Cepat"),
                    selectedIndex = uiState.pdfFilterMode,
                    onOptionSelected = { viewModel.setPdfFilterMode(it) },
                )

                when (uiState.pdfFilterMode) {
                    0 -> { // PILIHAN BULAN TERTENTU
                        val months = listOf(
                            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
                        )
                        val years = listOf("2024", "2025", "2026", "2027", "2028", "2029", "2030")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                        ) {
                            // Month Dropdown
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Bulan", style = MaterialTheme.typography.labelMedium)
                                DropdownMenuSelector(
                                    items = months.mapIndexed { idx, name -> idx.toString() to name },
                                    selectedId = uiState.pdfSelectedMonth.toString(),
                                    onItemSelected = { monthStr ->
                                        viewModel.setPdfMonthYear(monthStr.toInt(), uiState.pdfSelectedYear)
                                    },
                                )
                            }

                            // Year Dropdown
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Tahun", style = MaterialTheme.typography.labelMedium)
                                DropdownMenuSelector(
                                    items = years.map { it to it },
                                    selectedId = uiState.pdfSelectedYear.toString(),
                                    onItemSelected = { yearStr ->
                                        viewModel.setPdfMonthYear(uiState.pdfSelectedMonth, yearStr.toInt())
                                    },
                                )
                            }
                        }
                    }

                    1 -> { // RENTANG TANGGAL A - B
                        val dateFmt = remember { SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID")) }
                        val now = System.currentTimeMillis()
                        val startVal = uiState.pdfStartDate ?: (now - 30 * 86400000L)
                        val endVal = uiState.pdfEndDate ?: now

                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS)) {
                            Text(
                                text = "Periode Terpilih: ${dateFmt.format(Date(startVal))} s.d. ${dateFmt.format(Date(endVal))}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val cal = Calendar.getInstance()
                                        cal.add(Calendar.DAY_OF_MONTH, -7)
                                        viewModel.setPdfCustomDateRange(cal.timeInMillis, System.currentTimeMillis())
                                    },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("7 Hari Terakhir", style = MaterialTheme.typography.labelSmall)
                                }
                                OutlinedButton(
                                    onClick = {
                                        val cal = Calendar.getInstance()
                                        cal.add(Calendar.DAY_OF_MONTH, -30)
                                        viewModel.setPdfCustomDateRange(cal.timeInMillis, System.currentTimeMillis())
                                    },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("30 Hari Terakhir", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    2 -> { // PRESET CEPAT
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            presets.forEachIndexed { index, title ->
                                SegmentedButton(
                                    selected = uiState.selectedPresetIndex == index,
                                    onClick = { viewModel.applyPeriodPreset(index) },
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = presets.size),
                                ) {
                                    Text(title, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.SM))

                // ACTION BUTTONS
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS)) {
                    PrimaryButton(
                        text = "Preview Surat (Laman Khusus)",
                        icon = Icons.Default.PictureAsPdf,
                        onClick = {
                            viewModel.preparePdfReportContent()
                            viewModel.closeExportBottomSheet()
                            onNavigateToPdfPreview()
                        },
                        fullWidth = true,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                    ) {
                        SecondaryButton(
                            text = "Batal",
                            onClick = { viewModel.closeExportBottomSheet() },
                            modifier = Modifier.weight(1f),
                        )
                        PrimaryButton(
                            text = if (uiState.isGeneratingPdf) "Membuat..." else "Generate PDF",
                            icon = Icons.Default.Download,
                            onClick = {
                                viewModel.preparePdfReportContent()
                                viewModel.exportPdf(context)
                                viewModel.closeExportBottomSheet()
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionReportRow(
    transaction: TransactionEntity,
    modifier: Modifier = Modifier,
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val isExpense = transaction.type == TransactionType.EXPENSE

    val icon: ImageVector = when (transaction.type) {
        TransactionType.INCOME -> Icons.Default.ArrowUpward
        TransactionType.EXPENSE -> Icons.Default.ArrowDownward
        TransactionType.TRANSFER -> Icons.Default.SwapHoriz
    }

    val iconBgColor = when (transaction.type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.secondaryContainer
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.errorContainer
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.tertiaryContainer
    }

    val iconColor = when (transaction.type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.onSecondaryContainer
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.onErrorContainer
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    val amountPrefix = if (isIncome) "+ " else if (isExpense) "- " else ""
    val amountColor = when (transaction.type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.secondary
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.onSurface
    }

    AppCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Surface(
                    shape = KanzunShapes.Pill,
                    color = iconBgColor,
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.MD))

                Column {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${transaction.categoryId.formatCategoryName()} \u2022 ${transaction.accountId.formatAccountName()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.SM))

            Text(
                text = "$amountPrefix${Money.of(transaction.amountInCents).formatRupiah()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SuratLaporanPreviewCard(
    reportContent: ReportContent,
    modifier: Modifier = Modifier,
) {
    val logoFile = if (reportContent.logoPath.isNotBlank()) File(reportContent.logoPath) else null
    val logoBitmap = remember(reportContent.logoPath) {
        if (logoFile != null && logoFile.exists()) {
            BitmapFactory.decodeFile(logoFile.absolutePath)?.asImageBitmap()
        } else null
    }

    val todayFormatted = remember { SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date()) }
    val cityLocation = remember(reportContent.mosqueAddress) {
        reportContent.mosqueAddress.split(",").lastOrNull()?.trim()?.ifBlank { "Jakarta" } ?: "Jakarta"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = KanzunShapes.Card,
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.MD),
            verticalArrangement = Arrangement.spacedBy(Spacing.SM),
        ) {
            // 1. KOP SURAT
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
            ) {
                if (logoBitmap != null) {
                    Image(
                        bitmap = logoBitmap,
                        contentDescription = "Logo Masjid",
                        modifier = Modifier.size(44.dp),
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = KanzunShapes.Pill,
                        color = Color(0xFFF1F5F9),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = "Logo Fallback",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reportContent.mosqueName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                    )
                    Text(
                        text = reportContent.mosqueAddress,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569),
                    )
                    Text(
                        text = "Telp: ${reportContent.mosquePhone} \u2022 Email: ${reportContent.mosqueEmail}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                    )
                }
            }

            // Divider Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color(0xFF0F172A)),
            )

            Spacer(modifier = Modifier.height(Spacing.XS))

            // 2. JUDUL SURAT & PERIODE
            Text(
                text = "SURAT LAPORAN ${reportContent.reportType.title.uppercase()}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Periode: ${reportContent.periodLabel}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF475569),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            // 3. RINGKASAN
            if (reportContent.summaries.isNotEmpty()) {
                Surface(
                    shape = KanzunShapes.Card,
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.SM),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        reportContent.summaries.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF334155),
                                )
                                Text(
                                    text = item.value,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                )
                            }
                        }
                    }
                }
            }

            // 4. TABEL LAPORAN PREVIEW
            if (reportContent.tableHeaders.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFCBD5E1)),
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B))
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                    ) {
                        reportContent.tableHeaders.forEach { header ->
                            Text(
                                text = header,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    // Table Rows (take first 8 for clean preview)
                    reportContent.tableRows.take(8).forEachIndexed { index, row ->
                        val bg = if (index % 2 == 1) Color(0xFFF8FAFC) else Color.White
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bg)
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                        ) {
                            row.columns.forEach { text ->
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                    if (reportContent.tableRows.size > 8) {
                        Text(
                            text = "... dan ${reportContent.tableRows.size - 8} baris lainnya dalam dokumen PDF",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(6.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.XS))

            // 5. PENUTUP
            Text(
                text = "Demikian surat laporan ini dibuat dengan sebenarnya sebagai bentuk pertanggungjawaban pengelolaan keuangan masjid.",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF475569),
                fontStyle = FontStyle.Italic,
            )

            Spacer(modifier = Modifier.height(Spacing.XS))

            // 6. TANDA TANGAN
            Text(
                text = "$cityLocation, $todayFormatted",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF475569),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Mengetahui,", style = MaterialTheme.typography.labelSmall, color = Color(0xFF475569))
                    Text("Ketua DKM Masjid", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(36.dp))
                    Text("( ${reportContent.chairmanName} )", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0F172A))
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text("Dibuat oleh,", style = MaterialTheme.typography.labelSmall, color = Color(0xFF475569))
                    Text("Bendahara", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(36.dp))
                    Text("( ${reportContent.treasurerName} )", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0F172A))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownMenuSelector(
    items: List<Pair<String, String>>,
    selectedId: String,
    onItemSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = items.find { it.first == selectedId }?.second ?: items.firstOrNull()?.second ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        AppTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.second) },
                    onClick = {
                        onItemSelected(item.first)
                        expanded = false
                    },
                )
            }
        }
    }
}
