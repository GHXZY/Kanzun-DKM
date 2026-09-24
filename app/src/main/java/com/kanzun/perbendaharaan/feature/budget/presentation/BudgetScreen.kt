package com.kanzun.perbendaharaan.feature.budget.presentation

import androidx.compose.foundation.layout.heightIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.kanzun.perbendaharaan.core.designsystem.components.AppTextField as OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.BackHandler
import com.kanzun.perbendaharaan.core.util.formatCategoryName
import com.kanzun.perbendaharaan.core.database.entity.BudgetEntity
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.ChipStatusType
import com.kanzun.perbendaharaan.core.designsystem.components.EmptyState
import com.kanzun.perbendaharaan.core.designsystem.components.ErrorState
import com.kanzun.perbendaharaan.core.designsystem.components.LoadingState
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.ResponsiveContentContainer
import com.kanzun.perbendaharaan.core.designsystem.components.SecondaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.StatusChip
import com.kanzun.perbendaharaan.core.model.Money
import java.util.Calendar

private val PrimaryBlue: Color @Composable get() = MaterialTheme.colorScheme.primary
private val SecondaryGreen: Color @Composable get() = MaterialTheme.colorScheme.secondary
private val AccentAmber = Color(0xFFF9B637)

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    viewModel: BudgetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler(enabled = uiState.selectedBudgetForDetail != null) {
        viewModel.closeDetail()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (uiState.selectedBudgetForDetail == null) {
                FloatingActionButton(
                    onClick = { viewModel.openCreateDialog() },
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp),
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Buat RAPBM", modifier = Modifier.size(24.dp))
                }
            }
        },
    ) { innerPadding ->
        ResponsiveContentContainer {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                if (uiState.selectedBudgetForDetail == null) {
                    // STATE 1 - LIST RAPBM SCREEN
                    BudgetListView(
                        uiState = uiState,
                        onSearchChange = { viewModel.onSearchQueryChanged(it) },
                        onBudgetClick = { viewModel.openDetail(it) },
                        onRetry = {},
                    )
                } else {
                    // STATE 3 - DETAIL RAPBM VIEW
                    uiState.selectedBudgetForDetail?.let { detailBudget ->
                        BudgetDetailView(
                            budget = detailBudget,
                            uiState = uiState,
                            onBack = { viewModel.closeDetail() },
                            onEdit = { viewModel.openEditDialog(detailBudget) },
                            onDelete = { viewModel.openDeleteDialog(detailBudget) },
                        )
                    }
                }
            }
        }
    }

    // DIALOGS
    if (uiState.isCreateEditOpen) {
        CreateEditBudgetDialog(
            categories = uiState.categories,
            existingBudget = uiState.selectedBudgetForAction,
            existingItems = uiState.detailIncomeItems + uiState.detailExpenseItems,
            onDismiss = { viewModel.closeCreateEditDialog() },
            onSave = { monthTitle, year, incMap, expMap ->
                viewModel.saveMonthlyBudget(
                    monthYearTitle = monthTitle,
                    year = year,
                    incomePlannedMap = incMap,
                    expensePlannedMap = expMap,
                    existingBudgetId = uiState.selectedBudgetForAction?.id,
                )
            },
        )
    }

    if (uiState.isDeleteConfirmOpen) {
        uiState.selectedBudgetForAction?.let { actionBudget ->
            DeleteBudgetDialog(
                budgetTitle = actionBudget.title,
                onDismiss = { viewModel.closeDeleteDialog() },
                onConfirm = { viewModel.deleteBudget(actionBudget) },
            )
        }
    }
}

// -----------------------------------------------------------------------------
// STATE 1 - LIST RAPBM VIEW
// -----------------------------------------------------------------------------

@Composable
private fun BudgetListView(
    uiState: BudgetUiState,
    onSearchChange: (String) -> Unit,
    onBudgetClick: (BudgetEntity) -> Unit,
    onRetry: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // SEARCH BAR
        OutlinedTextField(
            value = uiState.searchQuery,
            onSearchChange,
            placeholder = { Text("Cari RAPBM...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Cari") },
            singleLine = true,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp),
        )

        // LIST RAPBM
        when {
            uiState.isLoading -> {
                LoadingState(count = 3)
            }

            uiState.errorMessage != null -> {
                ErrorState(
                    title = "Gagal Memuat RAPBM",
                    message = uiState.errorMessage!!,
                    onRetry = onRetry,
                )
            }

            uiState.filteredBudgets.isEmpty() -> {
                EmptyState(
                    title = "Belum Ada RAPBM",
                    description = if (uiState.searchQuery.isBlank()) "Belum ada rencana anggaran yang dibuat." else "RAPBM tidak ditemukan.",
                )
            }

            else -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    uiState.filteredBudgets.forEach { budget ->
                        BudgetCardItem(
                            budget = budget,
                            onClick = { onBudgetClick(budget) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetCardItem(
    budget: BudgetEntity,
    onClick: () -> Unit,
) {
    val dateRangeText = remember(budget.title, budget.year) {
        getPeriodDateRangeText(budget)
    }

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = budget.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                StatusChip(
                    text = "RAPBM",
                    type = ChipStatusType.INFO,
                )
            }

            Text(
                text = dateRangeText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Rancangan Pemasukan",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = Money.of(budget.totalIncomeBudgetInCents).formatRupiah(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                    color = PrimaryBlue,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Rancangan Pengeluaran",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = Money.of(budget.totalExpenseBudgetInCents).formatRupiah(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                    color = AccentAmber,
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// STATE 3 - DETAIL RAPBM VIEW
// -----------------------------------------------------------------------------

@Composable
private fun BudgetDetailView(
    budget: BudgetEntity,
    uiState: BudgetUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // TITLE HEADER FOR DETAIL VIEW
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = budget.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = getPeriodDateRangeText(budget),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // 1. FINANCIAL SUMMARY CARD
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Ringkasan Anggaran",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )

                // PEMASUKAN SUMMARY
                SummaryRowDual(
                    label = "Pemasukan",
                    plannedText = Money.of(uiState.totalPlannedIncomeInCents).formatRupiah(),
                    actualText = Money.of(uiState.totalActualIncomeInCents).formatRupiah(),
                    diffText = formatDiff(uiState.totalActualIncomeInCents - uiState.totalPlannedIncomeInCents, isIncome = true),
                )

                // PENGELUARAN SUMMARY
                SummaryRowDual(
                    label = "Pengeluaran",
                    plannedText = Money.of(uiState.totalPlannedExpenseInCents).formatRupiah(),
                    actualText = Money.of(uiState.totalActualExpenseInCents).formatRupiah(),
                    diffText = formatDiff(uiState.totalPlannedExpenseInCents - uiState.totalActualExpenseInCents, isIncome = false),
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Saldo Net (Pemasukan - Pengeluaran)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = Money.of(uiState.netActualBalanceInCents).formatRupiah(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp,
                            color = if (uiState.netActualBalanceInCents >= 0) SecondaryGreen else MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }

        // 2. CHART RANCANGAN VS REALITA
        BudgetGroupedBarChart(
            title = "Grafik Rancangan VS Realita",
            items = uiState.detailIncomeItems + uiState.detailExpenseItems,
            modifier = Modifier.fillMaxWidth(),
        )

        // 3. RINCIAN PER KATEGORI - PEMASUKAN
        Text(
            text = "Pemasukan (Rancangan vs Realita)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        if (uiState.detailIncomeItems.isEmpty()) {
            Text(text = "Belum ada rancangan pemasukan.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.detailIncomeItems.forEach { item ->
                    CategoryComparisonRow(item = item, isIncome = true)
                }
            }
        }

        // 4. RINCIAN PER KATEGORI - PENGELUARAN
        Text(
            text = "Pengeluaran (Rancangan vs Realita)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        if (uiState.detailExpenseItems.isEmpty()) {
            Text(text = "Belum ada rancangan pengeluaran.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.detailExpenseItems.forEach { item ->
                    CategoryComparisonRow(item = item, isIncome = false)
                }
            }
        }

        // 5. ACTION BUTTONS (EDIT & HAPUS)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SecondaryButton(
                text = "Edit RAPBM",
                icon = Icons.Default.Edit,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                onClick = onEdit,
            )
            SecondaryButton(
                text = "Hapus RAPBM",
                icon = Icons.Default.Delete,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                onClick = onDelete,
            )
        }
    }
}

@Composable
private fun SummaryRowDual(
    label: String,
    plannedText: String,
    actualText: String,
    diffText: String,
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Row {
                    Text(text = "Selisih: ", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Normal, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = diffText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row {
                    Text(
                        text = "Rancangan: ",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = plannedText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row {
                    Text(
                        text = "Realita: ",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = actualText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryComparisonRow(
    item: CategoryBudgetVsActual,
    isIncome: Boolean,
) {
    val diffCents = if (isIncome) {
        item.actualAmountInCents - item.plannedAmountInCents
    } else {
        item.plannedAmountInCents - item.actualAmountInCents
    }

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.categoryName.formatCategoryName(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = formatDiff(diffCents, isIncome),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (diffCents >= 0) SecondaryGreen else MaterialTheme.colorScheme.error,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row {
                    Text(
                        text = "Rancangan: ",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = Money.of(item.plannedAmountInCents).formatRupiah(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row {
                    Text(
                        text = "Realita: ",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = Money.of(item.actualAmountInCents).formatRupiah(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                    )
                }
            }
        }
    }
}

private fun getPeriodDateRangeText(budget: BudgetEntity): String {
    val monthNames = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
    var monthIdx = 8 // default September
    monthNames.forEachIndexed { idx, m ->
        if (budget.title.contains(m, ignoreCase = true)) monthIdx = idx
    }

    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, budget.year)
    cal.set(Calendar.MONTH, monthIdx)
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val shortMonth = monthNames[monthIdx].take(3)

    return "01 $shortMonth \u2013 $maxDays $shortMonth ${budget.year}"
}

private fun formatDiff(diffCents: Long, isIncome: Boolean): String {
    val absVal = Money.of(Math.abs(diffCents)).formatRupiah()
    return if (diffCents >= 0) "+$absVal" else "-$absVal"
}
