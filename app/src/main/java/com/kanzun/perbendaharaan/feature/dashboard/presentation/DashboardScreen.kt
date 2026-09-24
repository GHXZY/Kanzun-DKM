package com.kanzun.perbendaharaan.feature.dashboard.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.EmptyState
import com.kanzun.perbendaharaan.core.designsystem.components.ErrorState
import com.kanzun.perbendaharaan.core.designsystem.components.KpiCard
import com.kanzun.perbendaharaan.core.designsystem.components.LoadingState
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.ProgressCard
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.model.CashBreakdown
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionType
import com.kanzun.perbendaharaan.feature.dashboard.presentation.components.FundAllocationPieChart
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
                    // 2. TOTAL KAS (Styled card with Rekening vs Cash progress indicator)
                    TotalKasCard(
                        totalCash = state.totalCash,
                        cashBreakdown = state.cashBreakdown,
                        monthlyChange = state.monthlyChange,
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
                    shape = KanzunShapes.SmallComponent,
                    color = iconBgColor,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, iconColor.copy(alpha = 0.25f)),
                    modifier = Modifier.size(36.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.MD))

                Column {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "${transaction.categoryId.formatCategoryName()} \u2022 $formattedDate",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Text(
                text = "$amountPrefix${Money.of(transaction.amountInCents).formatRupiah()}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                ),
                color = amountColor,
            )
        }
    }
}

@Composable
private fun TotalKasCard(
    totalCash: Money,
    cashBreakdown: CashBreakdown,
    monthlyChange: Money,
    modifier: Modifier = Modifier,
    onCardClick: (() -> Unit)? = null,
) {
    val totalCents = totalCash.amountInCents
    val netChangeCents = monthlyChange.amountInCents
    val prevBalanceCents = totalCents - netChangeCents

    val (pctText, isPositive) = when {
        prevBalanceCents > 0 -> {
            val pct = (netChangeCents.toDouble() / prevBalanceCents.toDouble()) * 100.0
            val sign = if (pct >= 0) "+" else ""
            Pair("$sign${String.format(Locale.US, "%.1f", pct)}%", pct >= 0)
        }
        prevBalanceCents == 0L && totalCents > 0 -> Pair("+100.0%", true)
        netChangeCents > 0 -> Pair("+100.0%", true)
        netChangeCents < 0 -> Pair("-100.0%", false)
        else -> Pair("+0.0%", true)
    }

    val totalBreakdownCents = cashBreakdown.totalCash.amountInCents
    val bankCents = cashBreakdown.bankTotal.amountInCents
    val bankPct = if (totalBreakdownCents > 0) {
        (bankCents * 100 / totalBreakdownCents).toInt()
    } else 0
    val progressFraction = (bankPct / 100f).coerceIn(0f, 1f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onCardClick != null) Modifier.clickable(onClick = onCardClick) else Modifier),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.LG),
        ) {
            // Top Row: Icon Container on Left, Trend Badge on Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = "Total Kas Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isPositive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    ) {
                        Text(
                            text = pctText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                            ),
                            color = if (isPositive) Color(0xFF16A34A) else Color(0xFFDC2626),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "vs bulan lalu",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.LG))

            // Label: MONTHLY REVENUE -> TOTAL KAS
            Text(
                text = "TOTAL KAS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(Spacing.XS))

            // Big Amount: $12,480.00 -> Rp ...
            Text(
                text = totalCash.formatRupiah(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.64).sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(Spacing.LG))

            // Bottom Section: TARGET PROGRESS -> REKENING VS CASH
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "REKENING VS CASH",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = "$bankPct%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(modifier = Modifier.height(Spacing.XS + 2.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            ) {
                if (progressFraction > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progressFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.XS + 2.dp))

            // Detail amounts under progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Rekening: ${cashBreakdown.bankTotal.formatRupiah()}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Kas Tunai: ${cashBreakdown.cashTotal.formatRupiah()}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
