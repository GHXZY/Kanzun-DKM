package com.kanzun.perbendaharaan.feature.assets.presentation

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanzun.perbendaharaan.core.designsystem.components.AppDialog as Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzun.perbendaharaan.core.util.formatCategoryName
import com.kanzun.perbendaharaan.core.database.entity.AssetEntity
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.ChipStatusType
import com.kanzun.perbendaharaan.core.designsystem.components.EmptyState
import com.kanzun.perbendaharaan.core.designsystem.components.ErrorState
import com.kanzun.perbendaharaan.core.designsystem.components.FilterChip
import com.kanzun.perbendaharaan.core.designsystem.components.KpiCard
import com.kanzun.perbendaharaan.core.designsystem.components.LoadingState
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.ResponsiveContentContainer
import com.kanzun.perbendaharaan.core.designsystem.components.SecondaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.designsystem.components.StatusChip
import com.kanzun.perbendaharaan.core.model.Money
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatCategoryName(rawCategory: String): String {
    val clean = rawCategory.replace("cat_", "").replace("_", " ").trim()
    if (clean.isBlank()) return "Umum"
    return clean.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

@Composable
fun AssetsScreen(
    modifier: Modifier = Modifier,
    viewModel: AssetsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val conditionFilters = listOf("Semua", "Baik", "Dalam Perbaikan", "Rusak", "Hilang", "Diarsipkan")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddForm() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = KanzunShapes.Pill,
                modifier = Modifier.padding(bottom = Spacing.SM, end = Spacing.SM),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Aset",
                    modifier = Modifier.size(24.dp),
                )
            }
        },
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
                            title = "Gagal Memuat Aset",
                            message = uiState.errorMessage!!,
                            onRetry = { viewModel.loadAssets() },
                        )
                    }

                    else -> {
                        // 2. KPI / SUMMARY CARD
                        KpiCard(
                            title = "Total Nilai Aset",
                            valueText = uiState.totalAssetValue.formatRupiah(),
                            icon = Icons.Default.Inventory2,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        // 3. SEARCH INPUT
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Cari nama aset, lokasi, atau no inventaris...") },
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

                        // 4. CONDITION FILTER CHIPS
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                        ) {
                            conditionFilters.forEach { condition ->
                                val isSelected = (condition == "Semua" && uiState.selectedConditionFilter == null) ||
                                    (uiState.selectedConditionFilter == condition)
                                FilterChip(
                                    text = condition,
                                    selected = isSelected,
                                    onClick = {
                                        if (condition == "Semua") {
                                            viewModel.setConditionFilter(null)
                                        } else {
                                            viewModel.setConditionFilter(condition)
                                        }
                                    },
                                    modifier = Modifier.heightIn(min = 40.dp),
                                )
                            }
                        }

                        SectionHeader(
                            title = "Daftar Aset",
                        )

                        if (uiState.filteredAssets.isEmpty()) {
                            EmptyState(
                                title = "Tidak Ada Aset",
                                description = "Belum ada barang inventaris yang sesuai dengan filter pencarian.",
                                actionText = "Tambah Aset Baru",
                                onActionClick = { viewModel.openAddForm() },
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                                uiState.filteredAssets.forEach { asset ->
                                    AssetCardItem(
                                        asset = asset,
                                        onClick = { viewModel.openDetail(asset) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Wide Premium Form Dialog (Add / Edit)
    if (uiState.isFormOpen) {
        AssetFormDialog(
            isEditMode = uiState.isEditMode,
            initialAsset = uiState.selectedAsset,
            onDismiss = { viewModel.closeForm() },
            onSave = { name, category, valueCents, fundSource, location, condition, invNum ->
                viewModel.saveAsset(name, category, valueCents, fundSource, location, condition, invNum)
            },
        )
    }

    // Wide Detail Dialog
    if (uiState.selectedAsset != null && !uiState.isFormOpen) {
        AssetDetailDialog(
            asset = uiState.selectedAsset!!,
            onDismiss = { viewModel.closeDetail() },
            onEdit = { viewModel.openEditForm(uiState.selectedAsset!!) },
            onArchive = { viewModel.archiveOrDisposeAsset(uiState.selectedAsset!!) },
        )
    }
}

@Composable
private fun AssetCardItem(
    asset: AssetEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoryDisplay = remember(asset.categoryId) { formatCategoryName(asset.categoryId) }
    val formattedDate = remember(asset.acquisitionDate) {
        val df = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        df.format(Date(asset.acquisitionDate))
    }

    // Do NOT display status if it is "Draft"
    val hasValidStatus = asset.conditionStatus.isNotBlank() &&
        !asset.conditionStatus.equals("Draft", ignoreCase = true)

    val (statusText, statusType) = when (asset.conditionStatus) {
        "Baik" -> "Baik" to ChipStatusType.SUCCESS
        "Dalam Perbaikan" -> "Perbaikan" to ChipStatusType.WARNING
        "Rusak" -> "Rusak" to ChipStatusType.ERROR
        "Hilang" -> "Hilang" to ChipStatusType.ERROR
        else -> asset.conditionStatus to ChipStatusType.NEUTRAL
    }

    AppCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 68.dp),
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
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(42.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when {
                                categoryDisplay.contains("Elektronik", ignoreCase = true) -> Icons.Default.Category
                                categoryDisplay.contains("Ibadah", ignoreCase = true) -> Icons.Default.Inventory2
                                else -> Icons.Default.Inventory2
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp),
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
                            text = asset.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        if (hasValidStatus) {
                            StatusChip(text = statusText, type = statusType)
                        }
                    }
                    val cleanCat = categoryDisplay.formatCategoryName()
                    Text(
                        text = "$cleanCat \u2022 $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.SM))

            Text(
                text = Money.of(asset.acquisitionValueInCents).formatRupiah(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssetFormDialog(
    isEditMode: Boolean,
    initialAsset: AssetEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, Long, String, String, String, String) -> Unit,
) {
    var name by remember { mutableStateOf(initialAsset?.name ?: "") }
    var category by remember { mutableStateOf(formatCategoryName(initialAsset?.categoryId ?: "Elektronik")) }
    var valueText by remember { mutableStateOf(initialAsset?.acquisitionValueInCents?.toString() ?: "") }
    var fundSource by remember { mutableStateOf(initialAsset?.fundSourceId ?: "Dana Operasional") }
    var location by remember { mutableStateOf(initialAsset?.location ?: "Ruang Sholat Utama") }
    var condition by remember {
        mutableStateOf(
            if (initialAsset?.conditionStatus == "Draft" || initialAsset?.conditionStatus.isNullOrBlank()) "Baik"
            else initialAsset!!.conditionStatus
        )
    }
    var serialNumber by remember { mutableStateOf(initialAsset?.serialNumber ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categoryOptions = listOf("Elektronik", "Inventaris", "Bangunan", "Perlengkapan Ibadah", "Kendaraan", "Lainnya")
    val conditionOptions = listOf("Baik", "Dalam Perbaikan", "Rusak", "Hilang")

    var categoryExpanded by remember { mutableStateOf(false) }
    var conditionExpanded by remember { mutableStateOf(false) }

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
                // Compact Title Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (isEditMode) "Edit Aset" else "Tambah Aset",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
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

                // Scrollable Form Content
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

                    // 1. Nama Aset
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Nama Aset *",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("Contoh: Sound System Utama") },
                            singleLine = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 2. Kategori Dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Kategori Aset *",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = !categoryExpanded },
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                shape = KanzunShapes.Input,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp),
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false },
                            ) {
                                categoryOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            category = option
                                            categoryExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // 3. Nilai Perolehan
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Nilai Perolehan (Rp) *",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        OutlinedTextField(
                            value = valueText,
                            onValueChange = { valueText = it.filter { c -> c.isDigit() } },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 4. Sumber Dana
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Sumber Dana Perolehan",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        OutlinedTextField(
                            value = fundSource,
                            onValueChange = { fundSource = it },
                            placeholder = { Text("Contoh: Dana Operasional") },
                            singleLine = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 5. Lokasi Penempatan
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Lokasi Penempatan",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            placeholder = { Text("Contoh: Ruang Sholat Utama") },
                            singleLine = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }

                    // 6. Kondisi Aset Dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Kondisi Aset",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        ExposedDropdownMenuBox(
                            expanded = conditionExpanded,
                            onExpandedChange = { conditionExpanded = !conditionExpanded },
                        ) {
                            OutlinedTextField(
                                value = condition,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) },
                                shape = KanzunShapes.Input,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp),
                            )
                            ExposedDropdownMenu(
                                expanded = conditionExpanded,
                                onDismissRequest = { conditionExpanded = false },
                            ) {
                                conditionOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            condition = option
                                            conditionExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // 7. Nomor Inventaris / Serial Number
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Nomor Inventaris / Serial Number",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        OutlinedTextField(
                            value = serialNumber,
                            onValueChange = { serialNumber = it },
                            placeholder = { Text("INV-2026-001") },
                            singleLine = true,
                            shape = KanzunShapes.Input,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.LG))

                // Bottom Primary Action Button
                PrimaryButton(
                    text = "Simpan Aset",
                    onClick = {
                        val valueCents = valueText.toLongOrNull() ?: 0L
                        if (name.isBlank()) {
                            errorMessage = "Nama aset tidak boleh kosong"
                            return@PrimaryButton
                        }
                        onSave(name, category, valueCents, fundSource, location, condition, serialNumber)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp),
                )
            }
        }
    }
}

@Composable
private fun AssetDetailDialog(
    asset: AssetEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")) }
    val formattedDate = remember(asset.acquisitionDate) {
        dateFormat.format(Date(asset.acquisitionDate))
    }
    val cleanCategory = remember(asset.categoryId) { formatCategoryName(asset.categoryId) }

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
                        text = asset.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier.weight(1f),
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
                    DetailRow("Kategori", cleanCategory)
                    DetailRow("Nilai Perolehan", Money.of(asset.acquisitionValueInCents).formatRupiah())
                    DetailRow("Kondisi", if (asset.conditionStatus == "Draft") "Baik" else asset.conditionStatus)
                    DetailRow("Lokasi Penempatan", asset.location)
                    DetailRow("Sumber Dana", asset.fundSourceId)
                    DetailRow("Tanggal Perolehan", formattedDate)
                    if (asset.serialNumber.isNotBlank()) {
                        DetailRow("No Inventaris", asset.serialNumber)
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.LG))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                ) {
                    SecondaryButton(
                        text = "Edit",
                        onClick = onEdit,
                        icon = Icons.Default.Edit,
                        modifier = Modifier.weight(1f),
                    )
                    PrimaryButton(
                        text = "Arsipkan",
                        onClick = onArchive,
                        icon = Icons.Default.Archive,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.XS),
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
