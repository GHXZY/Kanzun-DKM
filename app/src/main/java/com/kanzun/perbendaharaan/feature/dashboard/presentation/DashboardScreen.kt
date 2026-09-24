package com.kanzun.perbendaharaan.feature.dashboard.presentation

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.EmptyState
import com.kanzun.perbendaharaan.core.designsystem.components.ErrorState
import com.kanzun.perbendaharaan.core.designsystem.components.HeroCard
import com.kanzun.perbendaharaan.core.designsystem.components.KpiCard
import com.kanzun.perbendaharaan.core.designsystem.components.LoadingState
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.ProgressCard
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.dashboard.presentation.components.FundAllocationPieChart
import com.kanzun.perbendaharaan.feature.dashboard.presentation.components.RekeningVsCashChart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


import com.kanzun.perbendaharaan.core.util.formatCategoryName
import com.kanzun.perbendaharaan.core.designsystem.components.ResponsiveContentContainer

@Composable
fun DashboardScreen(
    onNavigateToFeature: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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

        when (val state = uiState) {
            is DashboardUiState.Loading -> {
                LoadingState(count = 4)
            }

            is DashboardUiState.Error -> {
                ErrorState(
                    title = "Gagal Memuat Beranda",
                    message = state.message,
                    onRetry = { viewModel.loadDashboardData() },
                )
            }

            is DashboardUiState.Success -> {
                if (state.isEmpty) {
                    EmptyState(
                        title = "Belum Ada Data Kas",
                        description = "Mulai mencatat arus kas atau rekening masjid untuk melihat ringkasan keuangan.",
                        actionText = "Tambah Transaksi Baru",
                        onActionClick = { onNavigateToFeature("cash_flow") },
                    )
                } else {
                    // Calculate percentage breakdown safely
                    val totalCents = state.cashBreakdown.totalCash.amountInCents
                    val bankPct = if (totalCents > 0) (state.cashBreakdown.bankTotal.amountInCents * 100 / totalCents) else 0L
                    val cashPct = if (totalCents > 0) (state.cashBreakdown.cashTotal.amountInCents * 100 / totalCents) else 0L

                    // 2. TOTAL KAS (Hero Card)
                    HeroCard(
                        title = "Total Kas",
                        amountText = state.totalCash.formatRupiah(),
                    )

                    // 3. REKENING VS CASH (Pie / Donut Chart)
                    RekeningVsCashChart(
                        breakdown = state.cashBreakdown,
                        onCardClick = { onNavigateToFeature("cash_flow") },
                    )

                    // 5. STATUS KEUANGAN (Income, Expense, Net Cash Flow)
                    SectionHeader(
                        title = "Status Keuangan",
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.MD),
                    ) {
                        KpiCard(
                            title = "Pemasukan",
                            valueText = state.incomeThisMonth.formatRupiah(),
                            icon = Icons.Default.ArrowUpward,
                            iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f),
                        )

                        KpiCard(
                            title = "Pengeluaran",
                            valueText = state.expenseThisMonth.formatRupiah(),
                            icon = Icons.Default.ArrowDownward,
                            iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                            iconColor = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    KpiCard(
                        title = "Arus Kas Bersih",
                        valueText = state.netCashFlowThisMonth.formatRupiah(),
                        icon = Icons.Default.SwapHoriz,
                        iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // 6. PROGRESS TARGET DANA
                    SectionHeader(
                        title = "Target Dana",
                        actionText = "Kelola",
                        onActionClick = { onNavigateToFeature("fundraising") },
                    )

                    if (state.activeTarget != null) {
                        val target = state.activeTarget
                        val progress = if (target.targetAmountInCents > 0) {
                            target.collectedAmountInCents.toFloat() / target.targetAmountInCents.toFloat()
                        } else 0f

                        ProgressCard(
                            title = target.title,
                            currentAmountText = Money.of(target.collectedAmountInCents).formatRupiah(),
                            targetAmountText = Money.of(target.targetAmountInCents).formatRupiah(),
                            progress = progress,
                            badgeText = target.status,
                            onClick = { onNavigateToFeature("fundraising") },
                        )
                    } else {
                        AppCard(onClick = { onNavigateToFeature("fundraising") }) {
                            Text(
                                text = "Belum ada program target dana aktif.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    // 7. RECENT TRANSACTIONS
                    SectionHeader(
                        title = "Transaksi Terbaru",
                        actionText = "Lihat Semua",
                        onActionClick = { onNavigateToFeature("cash_flow") },
                    )

                    if (state.recentTransactions.isEmpty()) {
                        AppCard {
                            Text(
                                text = "Belum ada transaksi recorded.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                            state.recentTransactions.forEach { transaction ->
                                TransactionItemRow(
                                    transaction = transaction,
                                    onClick = { onNavigateToFeature("cash_flow") },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun TransactionItemRow(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
    val formattedDate = remember(transaction.timestamp) {
        dateFormat.format(Date(transaction.timestamp))
    }

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
        onClick = onClick,
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
                    )
                    Text(
                        text = "${transaction.categoryId.formatCategoryName()} \u2022 $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Text(
                text = "$amountPrefix${Money.of(transaction.amountInCents).formatRupiah()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor,
            )
        }
    }
}
