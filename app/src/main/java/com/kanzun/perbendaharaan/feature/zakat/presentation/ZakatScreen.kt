package com.kanzun.perbendaharaan.feature.zakat.presentation

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
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Person
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
import com.kanzun.perbendaharaan.core.database.entity.MustahikEntity
import com.kanzun.perbendaharaan.core.database.entity.ZakatTransactionEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ZakatScreen(
    modifier: Modifier = Modifier,
    viewModel: ZakatViewModel = hiltViewModel(),
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
                                onRetry = { viewModel.loadZakatData() },
                            )
                        }

                        else -> {
                            // 1. ZAKAT DASHBOARD SUMMARY CARDS
                            KpiCard(
                                title = "Saldo Dana Zakat",
                                valueText = uiState.currentZakatBalance.formatRupiah(),
                                icon = Icons.Default.CleanHands,
                                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.MD),
                            ) {
                                KpiCard(
                                    title = "Total Penerimaan",
                                    valueText = uiState.totalReceived.formatRupiah(),
                                    icon = Icons.Default.ArrowUpward,
                                    iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.weight(1f),
                                )

                                KpiCard(
                                    title = "Total Penyaluran",
                                    valueText = uiState.totalDistributed.formatRupiah(),
                                    icon = Icons.Default.ArrowDownward,
                                    iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                                    iconColor = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            // 2. COMPACT TABS: Muzakki | Mustahik | Penyaluran
                            SegmentedControl(
                                options = listOf("Muzakki", "Mustahik", "Penyaluran"),
                                selectedIndex = uiState.selectedTab,
                                onOptionSelected = { viewModel.setSelectedTab(it) },
                            )

                            when (uiState.selectedTab) {
                                0 -> { // Muzakki (Penerimaan)
                                    SectionHeader(
                                        title = "Daftar Muzakki & Penerimaan",
                                    )

                                    val receipts = uiState.zakatTransactions.filter { !it.isDistribution }
                                    if (receipts.isEmpty()) {
                                        EmptyState(
                                            title = "Belum Ada Muzakki",
                                            description = "Belum ada catatan penerimaan zakat dari muzakki.",
                                            actionText = "Tambah Muzakki",
                                            onActionClick = { viewModel.openMuzakkiForm() },
                                        )
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                                            receipts.forEach { tx ->
                                                MuzakkiReceiptRow(tx = tx)
                                            }
                                        }
                                    }
                                }

                                1 -> { // Mustahik
                                    SectionHeader(
                                        title = "Daftar Mustahik Terdaftar",
                                    )

                                    if (uiState.mustahiks.isEmpty()) {
                                        EmptyState(
                                            title = "Belum Ada Mustahik",
                                            description = "Belum ada data mustahik terdaftar di database.",
                                            actionText = "Tambah Mustahik",
                                            onActionClick = { viewModel.openMustahikForm() },
                                        )
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                                            uiState.mustahiks.forEach { m ->
                                                MustahikRow(
                                                    mustahik = m,
                                                    onEdit = { viewModel.openMustahikForm(m) },
                                                    onDelete = { viewModel.confirmDeleteMustahik(m) },
                                                )
                                            }
                                        }
                                    }
                                }

                                2 -> { // Penyaluran
                                    SectionHeader(
                                        title = "Daftar Penyaluran Zakat",
                                    )

                                    val distributions = uiState.zakatTransactions.filter { it.isDistribution }
                                    if (distributions.isEmpty()) {
                                        EmptyState(
                                            title = "Belum Ada Penyaluran",
                                            description = "Belum ada catatan penyaluran zakat kepada mustahik.",
                                            actionText = "Tambah Penyaluran",
                                            onActionClick = { viewModel.openPenyaluranForm() },
                                        )
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                                            distributions.forEach { tx ->
                                                PenyaluranRow(tx = tx)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. FAB BACKDROP SCRIM OVERLAY
        if (showFabMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable { showFabMenu = false },
            )
        }

        // 4. FAB SPEED DIAL WITH EXACTLY 3 ACTIONS (PRIMARY BLUE #0D47A1)
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
                        // Action 1: Muzakki
                        ZakatFabActionItem(
                            label = "Muzakki",
                            icon = Icons.Default.Person,
                            iconTint = MaterialTheme.colorScheme.primary,
                            onClick = {
                                showFabMenu = false
                                viewModel.openMuzakkiForm()
                            },
                        )

                        // Action 2: Daftar Mustahik
                        ZakatFabActionItem(
                            label = "Daftar Mustahik",
                            icon = Icons.Default.Group,
                            iconTint = MaterialTheme.colorScheme.secondary,
                            onClick = {
                                showFabMenu = false
                                viewModel.openMustahikForm()
                            },
                        )

                        // Action 3: Penyaluran
                        ZakatFabActionItem(
                            label = "Penyaluran",
                            icon = Icons.Default.NorthEast,
                            iconTint = MaterialTheme.colorScheme.error,
                            onClick = {
                                showFabMenu = false
                                viewModel.openPenyaluranForm()
                            },
                        )
                    }
                }

                // MAIN FAB ANCHOR (PRIMARY BLUE #0D47A1)
                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = KanzunShapes.Pill,
                    modifier = Modifier.size(56.dp),
                ) {
                    Icon(
                        imageVector = if (showFabMenu) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Menu Zakat",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }

    // POPUP 1: TAMBAH MUZAKKI
    if (uiState.activeForm == ZakatActionForm.MUZAKKI) {
        MuzakkiFormDialog(
            onDismiss = { viewModel.closeForm() },
            onSubmit = { name, zakatType, amount, note ->
                viewModel.addZakatReceipt(zakatType, amount, name, "acc_bsi", note)
            },
        )
    }

    // POPUP 2: TAMBAH / EDIT MUSTAHIK
    if (uiState.activeForm == ZakatActionForm.MUSTAHIK) {
        MustahikFormDialog(
            mustahikToEdit = uiState.mustahikToEdit,
            onDismiss = { viewModel.closeForm() },
            onSubmit = { id, name, asnaf, address, contact ->
                viewModel.saveMustahik(id, name, asnaf, address, contact)
            },
        )
    }

    // POPUP 3: TAMBAH PENYALURAN
    if (uiState.activeForm == ZakatActionForm.PENYALURAN) {
        PenyaluranFormDialog(
            mustahiks = uiState.mustahiks,
            onDismiss = { viewModel.closeForm() },
            onSubmit = { mustahikName, zakatType, amount, note ->
                viewModel.addZakatDistribution(mustahikName, zakatType, amount, "acc_cash", note)
            },
        )
    }

    // DELETE MUSTAHIK CONFIRMATION
    if (uiState.mustahikToDelete != null) {
        ConfirmDialog(
            title = "Hapus Mustahik",
            message = "Apakah Anda yakin ingin menghapus data mustahik \"${uiState.mustahikToDelete!!.name}\"?",
            confirmText = "Hapus",
            dismissText = "Batal",
            onConfirm = { viewModel.deleteMustahik(uiState.mustahikToDelete!!) },
            onDismiss = { viewModel.confirmDeleteMustahik(null) },
        )
    }
}

@Composable
private fun ZakatFabActionItem(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Surface(
            shape = KanzunShapes.SmallComponent,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
private fun MuzakkiReceiptRow(tx: ZakatTransactionEntity) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
    val formattedDate = remember(tx.timestamp) {
        dateFormat.format(Date(tx.timestamp))
    }

    AppCard(
        modifier = Modifier
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
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)),
                    modifier = Modifier.size(36.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.MD))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tx.muzakiOrMustahikName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${tx.zakatType} \u2022 $formattedDate",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (tx.note.isNotBlank()) {
                        Text(
                            text = tx.note,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Normal,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(Spacing.SM))

            Text(
                text = "+${Money.of(tx.amountInCents).formatRupiah()}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                ),
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

@Composable
private fun MustahikRow(
    mustahik: MustahikEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    AppCard(
        modifier = Modifier
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
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    modifier = Modifier.size(36.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.MD))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.XS),
                    ) {
                        Text(
                            text = mustahik.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Normal,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        StatusChip(text = mustahik.asnafCategory, type = ChipStatusType.INFO)
                    }
                    Text(
                        text = mustahik.address.ifBlank { "Alamat tidak diisi" },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (mustahik.phone.isNotBlank()) {
                        Text(
                            text = mustahik.phone,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Normal,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(Spacing.XS))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Mustahik",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Mustahik",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PenyaluranRow(tx: ZakatTransactionEntity) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
    val formattedDate = remember(tx.timestamp) {
        dateFormat.format(Date(tx.timestamp))
    }

    AppCard(
        modifier = Modifier
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
                    color = MaterialTheme.colorScheme.errorContainer,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.25f)),
                    modifier = Modifier.size(36.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.MD))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tx.muzakiOrMustahikName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${tx.zakatType} \u2022 $formattedDate",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (tx.note.isNotBlank()) {
                        Text(
                            text = tx.note,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Normal,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(Spacing.SM))

            Text(
                text = "-${Money.of(tx.amountInCents).formatRupiah()}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                ),
                color = MaterialTheme.colorScheme.error,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

// POPUP 1: TAMBAH MUZAKKI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MuzakkiFormDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, Long, String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var zakatType by remember { mutableStateOf("Zakat Mal") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val zakatOptions = listOf("Zakat Fitrah", "Zakat Mal", "Infak Zakat", "Zakat Penghasilan", "Zakat Perdagangan")
    var zakatExpanded by remember { mutableStateOf(false) }

    val currentDateStr = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Tambah Muzakki",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.MD))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Spacing.MD),
                ) {
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    // 1. Nama Muzakki
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Nama Muzakki", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("Nama Lengkap / Hamba Allah") },
                            singleLine = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 2. Jenis Zakat
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Jenis Zakat *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        ExposedDropdownMenuBox(
                            expanded = zakatExpanded,
                            onExpandedChange = { zakatExpanded = !zakatExpanded },
                        ) {
                            OutlinedTextField(
                                value = zakatType,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = zakatExpanded) },
                                shape = KanzunShapes.Input,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp),
                            )
                            ExposedDropdownMenu(
                                expanded = zakatExpanded,
                                onDismissRequest = { zakatExpanded = false },
                            ) {
                                zakatOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            zakatType = option
                                            zakatExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // 3. Tanggal
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Tanggal", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        OutlinedTextField(
                            value = currentDateStr,
                            onValueChange = {},
                            readOnly = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 4. Catatan
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Catatan", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            placeholder = { Text("Tulis rincian atau keterangan...") },
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 5. Nominal
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Nominal (Rp) *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
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
                }

                Spacer(modifier = Modifier.height(Spacing.LG))

                PrimaryButton(
                    text = "Simpan Muzakki",
                    onClick = {
                        val amountCents = amountText.toLongOrNull() ?: 0L
                        if (amountCents <= 0) {
                            errorMessage = "Nominal harus lebih besar dari 0"
                            return@PrimaryButton
                        }
                        onSubmit(name, zakatType, amountCents, note)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),
                )
            }
        }
    }
}

// POPUP 2: TAMBAH / EDIT MUSTAHIK
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MustahikFormDialog(
    mustahikToEdit: MustahikEntity?,
    onDismiss: () -> Unit,
    onSubmit: (String?, String, String, String, String) -> Unit,
) {
    val isEditMode = mustahikToEdit != null
    var name by remember { mutableStateOf(mustahikToEdit?.name ?: "") }
    var asnaf by remember { mutableStateOf(mustahikToEdit?.asnafCategory ?: "Fakir") }
    var address by remember { mutableStateOf(mustahikToEdit?.address ?: "") }
    var contact by remember { mutableStateOf(mustahikToEdit?.phone ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val asnafOptions = listOf("Fakir", "Miskin", "Gharim", "Riqab", "Muallaf", "Ibnu Sabil", "Amil", "Fi Sabilillah")
    var asnafExpanded by remember { mutableStateOf(false) }

    val currentDateStr = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (isEditMode) "Edit Mustahik" else "Tambah Mustahik",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.MD))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Spacing.MD),
                ) {
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    // 1. Nama Mustahik
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Nama Mustahik *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("Contoh: Abdullah") },
                            singleLine = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 2. Kategori Mustahik (Asnaf) Dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Kategori Mustahik *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        ExposedDropdownMenuBox(
                            expanded = asnafExpanded,
                            onExpandedChange = { asnafExpanded = !asnafExpanded },
                        ) {
                            OutlinedTextField(
                                value = asnaf,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = asnafExpanded) },
                                shape = KanzunShapes.Input,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp),
                            )
                            ExposedDropdownMenu(
                                expanded = asnafExpanded,
                                onDismissRequest = { asnafExpanded = false },
                            ) {
                                asnafOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            asnaf = option
                                            asnafExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // 3. Tanggal
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Tanggal", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        OutlinedTextField(
                            value = currentDateStr,
                            onValueChange = {},
                            readOnly = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 4. Alamat Tempat Tinggal
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Alamat Tempat Tinggal", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            placeholder = { Text("Contoh: RT 02 RW 04") },
                            singleLine = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 5. No Telepon / Kontak
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "No Telepon / Kontak", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        OutlinedTextField(
                            value = contact,
                            onValueChange = { contact = it },
                            placeholder = { Text("08123456789") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.LG))

                PrimaryButton(
                    text = "Simpan Mustahik",
                    onClick = {
                        if (name.isBlank()) {
                            errorMessage = "Nama mustahik harus diisi"
                            return@PrimaryButton
                        }
                        onSubmit(mustahikToEdit?.id, name, asnaf, address, contact)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),
                )
            }
        }
    }
}

// POPUP 3: TAMBAH PENYALURAN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PenyaluranFormDialog(
    mustahiks: List<MustahikEntity>,
    onDismiss: () -> Unit,
    onSubmit: (String, String, Long, String) -> Unit,
) {
    var selectedMustahikName by remember { mutableStateOf(mustahiks.firstOrNull()?.name ?: "") }
    var zakatType by remember { mutableStateOf("Penyaluran Fitrah") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val penyaluranOptions = listOf("Penyaluran Fitrah", "Penyaluran Maal", "Santunan Tunai", "Paket Sembako", "Beasiswa / Pendidikan", "Bantuan Usaha")

    var mustahikExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val currentDateStr = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Tambah Penyaluran",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.MD))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Spacing.MD),
                ) {
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    // 1. Nama Mustahik (Populated from mustahiks list)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Nama Mustahik *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        if (mustahiks.isEmpty()) {
                            Text(
                                text = "Belum ada mustahik (Silakan daftarkan mustahik terlebih dahulu)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        } else {
                            ExposedDropdownMenuBox(
                                expanded = mustahikExpanded,
                                onExpandedChange = { mustahikExpanded = !mustahikExpanded },
                            ) {
                                OutlinedTextField(
                                    value = selectedMustahikName,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mustahikExpanded) },
                                    shape = KanzunShapes.Input,
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                        .heightIn(min = 50.dp),
                                )
                                ExposedDropdownMenu(
                                    expanded = mustahikExpanded,
                                    onDismissRequest = { mustahikExpanded = false },
                                ) {
                                    mustahiks.forEach { m ->
                                        DropdownMenuItem(
                                            text = { Text("${m.name} (${m.asnafCategory})") },
                                            onClick = {
                                                selectedMustahikName = m.name
                                                mustahikExpanded = false
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Jenis Penyaluran Dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Jenis Penyaluran *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = !typeExpanded },
                        ) {
                            OutlinedTextField(
                                value = zakatType,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                shape = KanzunShapes.Input,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp),
                            )
                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false },
                            ) {
                                penyaluranOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            zakatType = option
                                            typeExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // 3. Tanggal
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Tanggal", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        OutlinedTextField(
                            value = currentDateStr,
                            onValueChange = {},
                            readOnly = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 4. Catatan
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Catatan", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            placeholder = { Text("Tulis rincian atau keterangan...") },
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 5. Nominal
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Nominal (Rp) *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
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
                }

                Spacer(modifier = Modifier.height(Spacing.LG))

                PrimaryButton(
                    text = "Simpan Penyaluran",
                    onClick = {
                        val amountCents = amountText.toLongOrNull() ?: 0L
                        if (amountCents <= 0) {
                            errorMessage = "Nominal harus lebih besar dari 0"
                            return@PrimaryButton
                        }
                        if (selectedMustahikName.isBlank()) {
                            errorMessage = "Nama mustahik harus dipilih/diisi"
                            return@PrimaryButton
                        }
                        onSubmit(selectedMustahikName, zakatType, amountCents, note)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),
                )
            }
        }
    }
}
