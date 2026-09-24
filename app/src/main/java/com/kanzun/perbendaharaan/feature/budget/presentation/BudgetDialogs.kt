package com.kanzun.perbendaharaan.feature.budget.presentation

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.kanzun.perbendaharaan.core.designsystem.components.AppTextField as OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kanzun.perbendaharaan.core.designsystem.components.AppDialog as Dialog
import androidx.compose.ui.window.DialogProperties
import com.kanzun.perbendaharaan.core.database.entity.BudgetEntity
import com.kanzun.perbendaharaan.core.database.entity.CategoryEntity
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.SecondaryButton
import com.kanzun.perbendaharaan.core.model.TransactionType

private val PrimaryBlue: Color @Composable get() = MaterialTheme.colorScheme.primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditBudgetDialog(
    categories: List<CategoryEntity>,
    existingBudget: BudgetEntity? = null,
    existingItems: List<CategoryBudgetVsActual> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (monthTitle: String, year: Int, incomePlannedMap: Map<String, Long>, expensePlannedMap: Map<String, Long>) -> Unit,
) {
    val months = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    val years = listOf(2025, 2026, 2027, 2028, 2029)

    var selectedMonth by remember { mutableStateOf("September") }
    var selectedYear by remember { mutableStateOf(2026) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Selected categories & planned amounts maps
    val incomePlannedMap = remember { mutableStateMapOf<String, String>() }
    val expensePlannedMap = remember { mutableStateMapOf<String, String>() }

    // Pre-populate if editing
    remember(existingBudget, existingItems) {
        if (existingBudget != null) {
            val title = existingBudget.title
            months.forEach { m ->
                if (title.contains(m, ignoreCase = true)) selectedMonth = m
            }
            selectedYear = existingBudget.year

            existingItems.forEach { item ->
                if (item.isExpense) {
                    expensePlannedMap[item.categoryId] = (item.plannedAmountInCents / 100).toString()
                } else {
                    incomePlannedMap[item.categoryId] = (item.plannedAmountInCents / 100).toString()
                }
            }
        } else {
            // Default selection
            categories.filter { it.type == TransactionType.INCOME }.take(3).forEach { cat ->
                incomePlannedMap[cat.id] = "10000000"
            }
            categories.filter { it.type == TransactionType.EXPENSE }.take(3).forEach { cat ->
                expensePlannedMap[cat.id] = "2000000"
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // HEADER: TITLE + CLOSE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (existingBudget != null) "Edit RAPBM" else "Buat RAPBM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", modifier = Modifier.size(24.dp))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    // PERIODE BULAN & TAHUN
                    Text(text = "Periode Anggaran *", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Month Dropdown
                        var monthExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = monthExpanded,
                            onExpandedChange = { monthExpanded = !monthExpanded },
                            modifier = Modifier.weight(1.5f),
                        ) {
                            OutlinedTextField(
                                value = selectedMonth,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Bulan") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).menuAnchor(),
                            )
                            ExposedDropdownMenu(expanded = monthExpanded, onDismissRequest = { monthExpanded = false }) {
                                months.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m) },
                                        onClick = {
                                            selectedMonth = m
                                            monthExpanded = false
                                        },
                                    )
                                }
                            }
                        }

                        // Year Dropdown
                        var yearExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = yearExpanded,
                            onExpandedChange = { yearExpanded = !yearExpanded },
                            modifier = Modifier.weight(1f),
                        ) {
                            OutlinedTextField(
                                value = selectedYear.toString(),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tahun") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).menuAnchor(),
                            )
                            ExposedDropdownMenu(expanded = yearExpanded, onDismissRequest = { yearExpanded = false }) {
                                years.forEach { y ->
                                    DropdownMenuItem(
                                        text = { Text(y.toString()) },
                                        onClick = {
                                            selectedYear = y
                                            yearExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // PERIODE TANGGAL AUTO INFO
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Periode RAPBM: 01 $selectedMonth $selectedYear \u2013 Akhir $selectedMonth $selectedYear",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(10.dp),
                        )
                    }

                    // KATEGORI PEMASUKAN
                    Text(text = "Kategori Pemasukan & Rancangan (Rp)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                    val incomeCats = categories.filter { it.type == TransactionType.INCOME }.ifEmpty {
                        listOf(
                            CategoryEntity("cat_infaq", "Infaq", TransactionType.INCOME),
                            CategoryEntity("cat_sedekah", "Sedekah", TransactionType.INCOME),
                            CategoryEntity("cat_donasi", "Donasi", TransactionType.INCOME),
                        )
                    }

                    incomeCats.forEach { cat ->
                        val isChecked = incomePlannedMap.containsKey(cat.id)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        incomePlannedMap[cat.id] = "0"
                                    } else {
                                        incomePlannedMap.remove(cat.id)
                                    }
                                },
                            )
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                            if (isChecked) {
                                OutlinedTextField(
                                    value = incomePlannedMap[cat.id] ?: "",
                                    onValueChange = { input ->
                                        incomePlannedMap[cat.id] = input.filter { it.isDigit() }
                                    },
                                    placeholder = { Text("0") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.width(130.dp).heightIn(min = 50.dp),
                                )
                            }
                        }
                    }

                    // KATEGORI PENGELUARAN
                    Text(text = "Kategori Pengeluaran & Rancangan (Rp)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                    val expenseCats = categories.filter { it.type == TransactionType.EXPENSE }.ifEmpty {
                        listOf(
                            CategoryEntity("cat_operasional", "Operasional", TransactionType.EXPENSE),
                            CategoryEntity("cat_listrik", "Listrik", TransactionType.EXPENSE),
                            CategoryEntity("cat_air", "Air", TransactionType.EXPENSE),
                            CategoryEntity("cat_kebersihan", "Kebersihan", TransactionType.EXPENSE),
                            CategoryEntity("cat_honorarium", "Honorarium", TransactionType.EXPENSE),
                        )
                    }

                    expenseCats.forEach { cat ->
                        val isChecked = expensePlannedMap.containsKey(cat.id)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        expensePlannedMap[cat.id] = "0"
                                    } else {
                                        expensePlannedMap.remove(cat.id)
                                    }
                                },
                            )
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                            if (isChecked) {
                                OutlinedTextField(
                                    value = expensePlannedMap[cat.id] ?: "",
                                    onValueChange = { input ->
                                        expensePlannedMap[cat.id] = input.filter { it.isDigit() }
                                    },
                                    placeholder = { Text("0") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.width(130.dp).heightIn(min = 50.dp),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    PrimaryButton(
                        text = "Simpan RAPBM",
                        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                        onClick = {
                            if (incomePlannedMap.isEmpty() && expensePlannedMap.isEmpty()) {
                                errorMessage = "Pilih minimal satu kategori anggaran"
                                return@PrimaryButton
                            }

                            val incParsed = incomePlannedMap.mapValues { (it.value.toLongOrNull() ?: 0L) * 100L }
                            val expParsed = expensePlannedMap.mapValues { (it.value.toLongOrNull() ?: 0L) * 100L }

                            onSave(
                                "RAPBM $selectedMonth $selectedYear",
                                selectedYear,
                                incParsed,
                                expParsed,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteBudgetDialog(
    budgetTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Hapus RAPBM", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", modifier = Modifier.size(24.dp))
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Apakah Anda yakin ingin menghapus $budgetTitle?\nData transaksi Arus Kas & saldo kas TIDAK akan dihapus.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SecondaryButton(text = "Batal", modifier = Modifier.weight(1f).heightIn(min = 48.dp), onClick = onDismiss)
                    PrimaryButton(text = "Hapus RAPBM", modifier = Modifier.weight(1f).heightIn(min = 48.dp), onClick = onConfirm)
                }
            }
        }
    }
}
