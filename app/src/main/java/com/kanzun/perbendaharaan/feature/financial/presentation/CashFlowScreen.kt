package com.kanzun.perbendaharaan.feature.financial.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.kanzun.perbendaharaan.core.designsystem.components.AppTextField as OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanzun.perbendaharaan.core.designsystem.components.AppDialog as Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzun.perbendaharaan.core.database.entity.TransactionEntity
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.ChipStatusType
import com.kanzun.perbendaharaan.core.designsystem.components.ConfirmDialog
import com.kanzun.perbendaharaan.core.designsystem.components.EmptyState
import com.kanzun.perbendaharaan.core.designsystem.components.ErrorState
import com.kanzun.perbendaharaan.core.designsystem.components.KpiCard
import com.kanzun.perbendaharaan.core.designsystem.components.LoadingState
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.ResponsiveContentContainer
import com.kanzun.perbendaharaan.core.designsystem.components.SecondaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.designsystem.components.SegmentedControl
import com.kanzun.perbendaharaan.core.designsystem.components.StatusChip
import com.kanzun.perbendaharaan.core.model.Money
import com.kanzun.perbendaharaan.core.model.TransactionStatus
import com.kanzun.perbendaharaan.core.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.kanzun.perbendaharaan.core.util.formatAccountName

private fun formatCategoryName(rawCategory: String): String {
    val clean = rawCategory.replace("cat_", "").replace("_", " ").trim()
    if (clean.isBlank()) return "Umum"
    return clean.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

@Composable
fun CashFlowScreen(
    modifier: Modifier = Modifier,
    viewModel: CashFlowViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFabMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            val scrollState = rememberScrollState()

            ResponsiveContentContainer {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                        .padding(horizontal = Spacing.MD)
                        .padding(bottom = Spacing.Section + Spacing.XL),
                    verticalArrangement = Arrangement.spacedBy(Spacing.MD),
                ) {
                    Spacer(modifier = Modifier.height(Spacing.XS))

                    when {
                        uiState.isLoading -> {
                            LoadingState(count = 3)
                        }

                        uiState.errorMessage != null -> {
                            ErrorState(
                                title = "Terjadi Kesalahan",
                                message = uiState.errorMessage!!,
                                onRetry = { viewModel.loadData() },
                            )
                        }

                        else -> {
                            // 1. SUMMARY CARDS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.MD),
                            ) {
                                KpiCard(
                                    title = "Pemasukan",
                                    valueText = uiState.totalIncome.formatRupiah(),
                                    icon = Icons.Default.ArrowUpward,
                                    iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.weight(1f),
                                )

                                KpiCard(
                                    title = "Pengeluaran",
                                    valueText = uiState.totalExpense.formatRupiah(),
                                    icon = Icons.Default.ArrowDownward,
                                    iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                                    iconColor = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            KpiCard(
                                title = "Arus Kas Bersih",
                                valueText = uiState.netCashFlow.formatRupiah(),
                                icon = Icons.Default.SwapHoriz,
                                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.fillMaxWidth(),
                            )

                            // 2. SEGMENTED FILTER & SEARCH
                            SegmentedControl(
                                options = listOf("Semua", "Pemasukan", "Pengeluaran", "Transfer"),
                                selectedIndex = uiState.selectedSegment,
                                onOptionSelected = { viewModel.setSegment(it) },
                            )

                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = { Text("Cari judul, catatan, atau no referensi...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    if (uiState.searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = KanzunShapes.Input,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp),
                            )

                            // 3. TRANSACTION LIST
                            SectionHeader(
                                title = "Daftar Transaksi Kas",
                            )

                            if (uiState.filteredTransactions.isEmpty()) {
                                EmptyState(
                                    title = "Tidak Ada Transaksi",
                                    description = "Belum ada transaksi yang sesuai dengan filter atau kata kunci pencarian Anda.",
                                    actionText = "Tambah Transaksi",
                                    onActionClick = { viewModel.openForm(TransactionType.INCOME) },
                                )
                            } else {
                                val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")) }
                                val groupedTransactions = remember(uiState.filteredTransactions) {
                                    uiState.filteredTransactions.groupBy { tx ->
                                        dateFormat.format(Date(tx.timestamp))
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                                    groupedTransactions.forEach { (dateHeader, transactions) ->
                                        Text(
                                            text = dateHeader,
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = Spacing.SM, bottom = Spacing.XS),
                                        )

                                        transactions.forEach { tx ->
                                            TransactionCardItem(
                                                transaction = tx,
                                                onClick = { viewModel.openDetail(tx) },
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

        // 4. FAB BACKDROP OVERLAY SCRIM
        if (showFabMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable { showFabMenu = false },
            )
        }

        // 5. MODERN VERTICAL SPEED DIAL FAB (SEJAJAR KE ATAS)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 24.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AnimatedVisibility(
                    visible = showFabMenu,
                    enter = fadeIn(tween(200)) + scaleIn(tween(200)),
                    exit = fadeOut(tween(180)) + scaleOut(tween(180)),
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        VerticalFabMenuItem(
                            label = "Pemasukan",
                            icon = Icons.Default.ArrowDownward,
                            iconTint = MaterialTheme.colorScheme.secondary,
                            onClick = {
                                showFabMenu = false
                                viewModel.openForm(TransactionType.INCOME)
                            },
                        )

                        VerticalFabMenuItem(
                            label = "Pengeluaran",
                            icon = Icons.Default.ArrowUpward,
                            iconTint = MaterialTheme.colorScheme.error,
                            onClick = {
                                showFabMenu = false
                                viewModel.openForm(TransactionType.EXPENSE)
                            },
                        )

                        VerticalFabMenuItem(
                            label = "Transfer",
                            icon = Icons.Default.SwapHoriz,
                            iconTint = MaterialTheme.colorScheme.primary,
                            onClick = {
                                showFabMenu = false
                                viewModel.openForm(TransactionType.TRANSFER)
                            },
                        )
                    }
                }

                // MAIN FAB ANCHOR
                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = KanzunShapes.Pill,
                    modifier = Modifier.size(56.dp),
                ) {
                    Icon(
                        imageVector = if (showFabMenu) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Menu Transaksi",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }

    // 7. TRANSACTION FORM DIALOG (Wider, Premium Modal)
    if (uiState.isFormOpen) {
        TransactionFormDialog(
            type = uiState.formType,
            accounts = uiState.accounts,
            funds = uiState.funds,
            categories = uiState.categories,
            onDismiss = { viewModel.closeForm() },
            onSubmitIncome = { title, amount, accountId, fundId, categoryId, note, ref ->
                viewModel.createIncome(title, amount, accountId, fundId, categoryId, note, ref)
            },
            onSubmitExpense = { title, amount, accountId, fundId, categoryId, note, ref ->
                viewModel.createExpense(title, amount, accountId, fundId, categoryId, note, ref)
            },
            onSubmitTransfer = { fromAccount, toAccount, amount, note, ref ->
                viewModel.createTransfer(fromAccount, toAccount, amount, note, ref)
            },
        )
    }

    // 8. TRANSACTION DETAIL DIALOG
    if (uiState.selectedTransaction != null) {
        TransactionDetailDialog(
            transaction = uiState.selectedTransaction!!,
            onDismiss = { viewModel.closeDetail() },
            onFinalizeClick = { viewModel.openFinalizeConfirm() },
            onReversalClick = { viewModel.openReversalConfirm() },
        )
    }

    // 9. FINALIZE CONFIRMATION DIALOG
    if (uiState.isFinalizeConfirmOpen && uiState.selectedTransaction != null) {
        ConfirmDialog(
            title = "Finalisasi Transaksi",
            message = "Transaksi yang telah difinalisasi bersifat permanen dan tidak dapat diubah secara langsung.",
            confirmText = "Ya, Finalisasi",
            dismissText = "Batal",
            onConfirm = { viewModel.finalizeTransaction(uiState.selectedTransaction!!.id) },
            onDismiss = { viewModel.closeFinalizeConfirm() },
        )
    }

    // 10. REVERSAL CONFIRMATION DIALOG
    if (uiState.isReversalConfirmOpen && uiState.selectedTransaction != null) {
        ConfirmDialog(
            title = "Koreksi / Reversal Transaksi",
            message = "Proses koreksi akan membuat transaksi pembalik yang menganulir efek transaksi ini secara auditable.",
            confirmText = "Proses Koreksi Reversal",
            dismissText = "Batal",
            onConfirm = {
                viewModel.reverseTransaction(
                    transactionId = uiState.selectedTransaction!!.id,
                    reason = "Koreksi kesalahan pencatatan kas",
                )
            },
            onDismiss = { viewModel.closeReversalConfirm() },
        )
    }
}

@Composable
private fun VerticalFabMenuItem(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Surface(
            shape = KanzunShapes.SmallComponent,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 4.dp,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }

        Surface(
            shape = KanzunShapes.SmallComponent,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.size(42.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun TransactionCardItem(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val isExpense = transaction.type == TransactionType.EXPENSE

    val categoryDisplay = remember(transaction.categoryId) { formatCategoryName(transaction.categoryId) }

    val icon = when (transaction.type) {
        TransactionType.INCOME -> Icons.Default.ArrowDownward
        TransactionType.EXPENSE -> Icons.Default.ArrowUpward
        TransactionType.TRANSFER -> Icons.Default.SwapHoriz
    }

    val iconBgColor = when (transaction.type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.secondaryContainer
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.errorContainer
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.primaryContainer
    }

    val iconColor = when (transaction.type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.onSecondaryContainer
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.onErrorContainer
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    val amountPrefix = if (isIncome) "+ " else if (isExpense) "- " else ""
    val amountColor = when (transaction.type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.secondary
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.onSurface
    }

    AppCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = categoryDisplay,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.SM))

            Text(
                text = "$amountPrefix${Money.of(transaction.amountInCents).formatRupiah()}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                ),
                color = amountColor,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionFormDialog(
    type: TransactionType,
    accounts: List<com.kanzun.perbendaharaan.core.database.entity.AccountEntity>,
    funds: List<com.kanzun.perbendaharaan.core.database.entity.FundEntity>,
    categories: List<com.kanzun.perbendaharaan.core.database.entity.CategoryEntity>,
    onDismiss: () -> Unit,
    onSubmitIncome: (String, Long, String, String, String, String, String) -> Unit,
    onSubmitExpense: (String, Long, String, String, String, String, String) -> Unit,
    onSubmitTransfer: (String, String, Long, String, String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var refNumber by remember { mutableStateOf("") }

    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: "") }
    var selectedToAccountId by remember { mutableStateOf(accounts.getOrNull(1)?.id ?: accounts.firstOrNull()?.id ?: "") }
    var selectedFundId by remember { mutableStateOf(funds.firstOrNull()?.id ?: "") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dialogTitle = when (type) {
        TransactionType.INCOME -> "Tambah Pemasukan"
        TransactionType.EXPENSE -> "Tambah Pengeluaran"
        TransactionType.TRANSFER -> "Tambah Transfer"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = KanzunShapes.Card,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                
                .padding(vertical = Spacing.MD),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.LG),
            ) {
                // Compact Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = dialogTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.MD))

                // Scrollable Form Body
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Spacing.MD),
                ) {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    // 1. Nominal Field
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Nominal (Rp) *",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                            placeholder = { Text("Rp 0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 2. Judul / Deskripsi Transaksi
                    if (type != TransactionType.TRANSFER) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Judul Transaksi *",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = { Text("Contoh: Donasi Jumat / Bayar Listrik") },
                                singleLine = true,
                                shape = KanzunShapes.Input,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp),
                            )
                        }
                    }

                    if (type == TransactionType.TRANSFER) {
                        // Source Account Dropdown
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Rekening Asal *",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            DropdownSelector(
                                items = accounts.map { it.id to it.name.formatAccountName() },
                                selectedId = selectedAccountId,
                                onItemSelected = { selectedAccountId = it },
                            )
                        }

                        // Destination Account Dropdown
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Rekening Tujuan *",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            DropdownSelector(
                                items = accounts.map { it.id to it.name.formatAccountName() },
                                selectedId = selectedToAccountId,
                                onItemSelected = { selectedToAccountId = it },
                            )
                        }
                    } else {
                        // Account Dropdown
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Rekening / Cash *",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            DropdownSelector(
                                items = accounts.map { it.id to it.name.formatAccountName() },
                                selectedId = selectedAccountId,
                                onItemSelected = { selectedAccountId = it },
                            )
                        }

                        // Fund Dropdown
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Peruntukan Dana *",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            DropdownSelector(
                                items = funds.map { it.id to it.name },
                                selectedId = selectedFundId,
                                onItemSelected = { selectedFundId = it },
                            )
                        }

                        // Category Dropdown
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Kategori *",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            DropdownSelector(
                                items = categories.filter { it.type == type }.map { it.id to formatCategoryName(it.name) },
                                selectedId = selectedCategoryId,
                                onItemSelected = { selectedCategoryId = it },
                            )
                        }
                    }

                    // Nomor Referensi
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Nomor Referensi (Opsional)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        OutlinedTextField(
                            value = refNumber,
                            onValueChange = { refNumber = it },
                            placeholder = { Text("Contoh: REF-2026-001") },
                            singleLine = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // Catatan Tambahan
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Catatan Tambahan",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            placeholder = { Text("Tulis rincian atau keterangan tambahan...") },
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.LG))

                // Primary Bottom Action Button
                PrimaryButton(
                    text = "Simpan Transaksi",
                    onClick = {
                        val amountCents = amountText.toLongOrNull() ?: 0L
                        if (amountCents <= 0) {
                            errorMessage = "Nominal harus lebih besar dari 0"
                            return@PrimaryButton
                        }

                        if (type == TransactionType.TRANSFER) {
                            if (selectedAccountId == selectedToAccountId) {
                                errorMessage = "Rekening asal dan tujuan tidak boleh sama"
                                return@PrimaryButton
                            }
                            onSubmitTransfer(
                                selectedAccountId,
                                selectedToAccountId,
                                amountCents,
                                note,
                                refNumber,
                            )
                        } else {
                            if (title.isBlank()) {
                                errorMessage = "Judul transaksi tidak boleh kosong"
                                return@PrimaryButton
                            }

                            if (type == TransactionType.INCOME) {
                                onSubmitIncome(
                                    title,
                                    amountCents,
                                    selectedAccountId,
                                    selectedFundId,
                                    selectedCategoryId,
                                    note,
                                    refNumber,
                                )
                            } else {
                                onSubmitExpense(
                                    title,
                                    amountCents,
                                    selectedAccountId,
                                    selectedFundId,
                                    selectedCategoryId,
                                    note,
                                    refNumber,
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelector(
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
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = KanzunShapes.Input,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .heightIn(min = 50.dp),
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

@Composable
private fun TransactionDetailDialog(
    transaction: TransactionEntity,
    onDismiss: () -> Unit,
    onFinalizeClick: () -> Unit,
    onReversalClick: () -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")) }
    val formattedDate = remember(transaction.timestamp) {
        dateFormat.format(Date(transaction.timestamp))
    }
    val cleanCategory = remember(transaction.categoryId) { formatCategoryName(transaction.categoryId) }

    val isDraft = transaction.status == TransactionStatus.DRAFT && !transaction.isReversed
    val isFinalized = transaction.status == TransactionStatus.FINALIZED && !transaction.isReversed

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = KanzunShapes.Card,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .padding(vertical = Spacing.MD),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.LG),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Detail Transaksi",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.MD))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.SM),
                ) {
                    DetailRow("Judul Transaksi", transaction.title)
                    DetailRow("Jenis Transaksi", transaction.type.name)
                    DetailRow("Nominal Kas", Money.of(transaction.amountInCents).formatRupiah(), isEmphasized = true)
                    DetailRow("Waktu Pencatatan", formattedDate)
                    DetailRow("Rekening / Kas", transaction.accountId.formatAccountName())
                    DetailRow("Peruntukan Dana", transaction.fundId)
                    DetailRow("Kategori", cleanCategory)
                    if (transaction.referenceNumber.isNotBlank()) {
                        DetailRow("No Referensi", transaction.referenceNumber)
                    }
                    if (transaction.note.isNotBlank()) {
                        DetailRow("Catatan", transaction.note)
                    }

                    if (isFinalized) {
                        Spacer(modifier = Modifier.height(Spacing.SM))
                        Surface(
                            shape = KanzunShapes.SmallComponent,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "Transaksi ini telah difinalisasi. Perubahan data hanya dapat dilakukan melalui prosedur Koreksi Reversal.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(Spacing.MD),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.LG))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                ) {
                    if (isDraft) {
                        PrimaryButton(
                            text = "Finalisasi",
                            onClick = onFinalizeClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (isFinalized) {
                        SecondaryButton(
                            text = "Koreksi / Reversal",
                            onClick = onReversalClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, isEmphasized: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.XS),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isEmphasized) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
