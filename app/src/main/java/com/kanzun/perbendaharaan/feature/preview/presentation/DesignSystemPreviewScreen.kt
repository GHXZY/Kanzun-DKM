package com.kanzun.perbendaharaan.feature.preview.presentation

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.AppIconButton
import com.kanzun.perbendaharaan.core.designsystem.components.AppOutlinedButton
import com.kanzun.perbendaharaan.core.designsystem.components.AppProgressBar
import com.kanzun.perbendaharaan.core.designsystem.components.ChipStatusType
import com.kanzun.perbendaharaan.core.designsystem.components.ConfirmDialog
import com.kanzun.perbendaharaan.core.designsystem.components.EmptyState
import com.kanzun.perbendaharaan.core.designsystem.components.ErrorState
import com.kanzun.perbendaharaan.core.designsystem.components.FilterChip
import com.kanzun.perbendaharaan.core.designsystem.components.HeroCard
import com.kanzun.perbendaharaan.core.designsystem.components.KpiCard
import com.kanzun.perbendaharaan.core.designsystem.components.LoadingState
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.ProgressCard
import com.kanzun.perbendaharaan.core.designsystem.components.SecondaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.designsystem.components.SegmentedControl
import com.kanzun.perbendaharaan.core.designsystem.components.StatusChip

@Composable
fun DesignSystemPreviewScreen(
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    var selectedSegment by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableIntStateOf(0) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        ConfirmDialog(
            title = "Konfirmasi Aksi",
            message = "Apakah Anda yakin ingin memproses aksi ini?",
            onConfirm = {},
            onDismiss = { showConfirmDialog = false },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = Spacing.MD)
            .padding(bottom = Spacing.XL),
        verticalArrangement = Arrangement.spacedBy(Spacing.XL),
    ) {
        Spacer(modifier = Modifier.height(Spacing.XS))

        SectionHeader(
            title = "Galeri Komponen UI",
            subtitle = "Sistem Desain Kanzun - Stage 2",
        )

        // 1. Buttons
        SectionHeader(title = "1. Buttons & Icon Buttons")
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.MD)) {
            PrimaryButton(
                text = "Primary Button",
                onClick = { showConfirmDialog = true },
                icon = Icons.Default.Add,
                fullWidth = true,
            )

            SecondaryButton(
                text = "Secondary Button",
                onClick = {},
                fullWidth = true,
            )

            AppOutlinedButton(
                text = "Outlined Button",
                onClick = {},
                fullWidth = true,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.MD)) {
                AppIconButton(
                    icon = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    onClick = {},
                )
                AppIconButton(
                    icon = Icons.Default.Check,
                    contentDescription = "Selesai",
                    onClick = {},
                )
            }
        }

        // 2. Cards
        SectionHeader(title = "2. Cards (AppCard, HeroCard, KpiCard, ProgressCard)")
        HeroCard(
            title = "HERO CARD - TOTAL KAS",
            amountText = "Rp 125.450.000",
            subtitle = "Rekening: Rp 100jt \u2022 Cash: Rp 25.450rb",
            badgeText = "Stabil",
        )

        KpiCard(
            title = "KPI CARD - PEMASUKAN",
            valueText = "Rp 32.500.000",
            subtitle = "+15% vs bulan lalu",
            icon = Icons.Default.AccountBalance,
        )

        ProgressCard(
            title = "PROGRESS CARD - TARGET DANA",
            currentAmountText = "Rp 75.000.000",
            targetAmountText = "Rp 100.000.000",
            progress = 0.75f,
            badgeText = "75%",
        )

        AppCard {
            Text(text = "Standard AppCard Container", style = MaterialTheme.typography.titleLarge)
            Text(text = "Surface elevated with 20dp border radius", style = MaterialTheme.typography.bodySmall)
        }

        // 3. Chips & Segmented Controls
        SectionHeader(title = "3. Status Chips, Filter Chips & Segmented Controls")
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.SM)) {
            StatusChip(text = "Pemasukan", type = ChipStatusType.SUCCESS)
            StatusChip(text = "Pengeluaran", type = ChipStatusType.ERROR)
            StatusChip(text = "Target", type = ChipStatusType.WARNING)
            StatusChip(text = "Info", type = ChipStatusType.INFO)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.SM)) {
            listOf("Semua", "Kas Bank", "Kas Tunai").forEachIndexed { index, title ->
                FilterChip(
                    text = title,
                    selected = selectedFilter == index,
                    onClick = { selectedFilter = index },
                )
            }
        }

        SegmentedControl(
            options = listOf("Semua", "Pemasukan", "Pengeluaran", "Transfer"),
            selectedIndex = selectedSegment,
            onOptionSelected = { selectedSegment = it },
        )

        // 4. Progress Bars
        SectionHeader(title = "4. Progress Bars")
        AppProgressBar(progress = 0.65f)

        // 5. States (Empty, Loading, Error)
        SectionHeader(title = "5. UI States (Empty, Loading, Error)")
        EmptyState(
            title = "Belum Ada Transaksi",
            description = "Pencatatan kas untuk periode ini belum tersedia.",
            actionText = "Tambah Transaksi",
            onActionClick = {},
        )

        ErrorState(
            title = "Gagal Memuat Data",
            message = "Terjadi kesalahan koneksi saat memuat data perbendaharaan.",
            onRetry = {},
        )

        LoadingState(count = 2)
    }
}
