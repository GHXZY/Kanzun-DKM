package com.kanzun.perbendaharaan.feature.settings.presentation

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzun.perbendaharaan.core.database.entity.BackupMetadataEntity
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.AppOutlinedButton
import com.kanzun.perbendaharaan.core.designsystem.components.AppDialog as Dialog
import com.kanzun.perbendaharaan.core.designsystem.components.AppTextField as OutlinedTextField
import com.kanzun.perbendaharaan.core.designsystem.components.ChipStatusType
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.ResponsiveContentContainer
import com.kanzun.perbendaharaan.core.designsystem.components.SecondaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.designsystem.components.StatusChip
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    onNavigateToAbout: () -> Unit = {},
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onNavigateToPreview: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var mosqueName by remember(uiState.mosqueProfile) {
        mutableStateOf(uiState.mosqueProfile?.name ?: "Masjid Agung Al-Mubarak")
    }
    var mosqueAddress by remember(uiState.mosqueProfile) {
        mutableStateOf(uiState.mosqueProfile?.address ?: "Jl. Ahmad Yani No. 45, Jakarta")
    }
    var treasurerName by remember(uiState.mosqueProfile) {
        mutableStateOf(uiState.mosqueProfile?.treasurerName?.ifBlank { "H. Muhammad Hatta" } ?: "H. Muhammad Hatta")
    }
    var dkmChairmanName by remember(uiState.mosqueProfile) {
        mutableStateOf(uiState.mosqueProfile?.dkmChairmanName?.ifBlank { "H. Ahmad Dahlan" } ?: "H. Ahmad Dahlan")
    }

    var cashOpeningBalanceText by remember(uiState.cashOpeningBalance) {
        mutableStateOf(if (uiState.cashOpeningBalance > 0) uiState.cashOpeningBalance.toString() else "")
    }
    var bankOpeningBalanceText by remember(uiState.bankOpeningBalance) {
        mutableStateOf(if (uiState.bankOpeningBalance > 0) uiState.bankOpeningBalance.toString() else "")
    }


    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) {
            viewModel.saveLogoFromUri(context, uri)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadPinState(context)
    }

    LaunchedEffect(uiState.message) {
        val msg = uiState.message
        if (msg != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    ResponsiveContentContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = Spacing.MD)
                .padding(bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.MD),
        ) {
            Spacer(modifier = Modifier.height(Spacing.XS))

            SectionHeader(
                title = "Pengaturan",
            )

            // CARD 1: IDENTITAS MASJID
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                    Text(
                        text = "Identitas Masjid",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    // Logo Preview & Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.MD),
                    ) {
                        val logoPath = uiState.mosqueProfile?.logoPath ?: ""
                        val logoFile = if (logoPath.isNotEmpty()) File(logoPath) else null

                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (logoFile != null && logoFile.exists()) {
                                    val bitmap = remember(logoFile.absolutePath) {
                                        BitmapFactory.decodeFile(logoFile.absolutePath)
                                    }
                                    if (bitmap != null) {
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Logo Masjid",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(4.dp)),
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Store,
                                            contentDescription = "Logo Default",
                                            modifier = Modifier.size(36.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = "Logo Default",
                                        modifier = Modifier.size(36.dp),
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS)) {
                            Text(
                                text = "Logo Masjid",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Normal,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.XS)) {
                                AppOutlinedButton(
                                    text = if (logoPath.isEmpty()) "Pilih Logo" else "Ubah Logo",
                                    icon = Icons.Default.Image,
                                    onClick = { logoPickerLauncher.launch("image/*") },
                                )

                                if (logoPath.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.removeLogo() }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus Logo",
                                            tint = MaterialTheme.colorScheme.error,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.XS))

                    OutlinedTextField(
                        value = mosqueName,
                        onValueChange = { mosqueName = it },
                        label = { Text("Nama Masjid") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = mosqueAddress,
                        onValueChange = { mosqueAddress = it },
                        label = { Text("Alamat Masjid") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // CARD 2: PENGURUS / PENANGGUNG JAWAB
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                    Text(
                        text = "Pengurus / Penanggung Jawab",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    OutlinedTextField(
                        value = treasurerName,
                        onValueChange = { treasurerName = it },
                        label = { Text("Nama Bendahara / User") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = dkmChairmanName,
                        onValueChange = { dkmChairmanName = it },
                        label = { Text("Nama Ketua DKM") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            }

            // SIMPAN IDENTITAS MASJID BUTTON
            PrimaryButton(
                text = "Simpan Identitas Masjid",
                onClick = {
                    val currentLogo = uiState.mosqueProfile?.logoPath ?: ""
                    viewModel.saveMosqueIdentity(
                        name = mosqueName,
                        address = mosqueAddress,
                        treasurerName = treasurerName,
                        dkmChairmanName = dkmChairmanName,
                        logoPath = currentLogo,
                    )
                },
                icon = Icons.Default.Save,
                fullWidth = true,
            )

            // CARD: SALDO AWAL (KAS TUNAI & REKENING BANK)
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                    Text(
                        text = "Saldo Awal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Atur saldo awal untuk akun kas tunai dan rekening bank masjid.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    OutlinedTextField(
                        value = cashOpeningBalanceText,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) cashOpeningBalanceText = input
                        },
                        label = { Text("Saldo Awal Kas Tunai (Rp)") },
                        prefix = { Text("Rp ") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = bankOpeningBalanceText,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) bankOpeningBalanceText = input
                        },
                        label = { Text("Saldo Awal Rekening Bank (Rp)") },
                        prefix = { Text("Rp ") },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(Spacing.XS))

                    PrimaryButton(
                        text = "Simpan Saldo Awal",
                        onClick = {
                            val cashCents = cashOpeningBalanceText.toLongOrNull() ?: 0L
                            val bankCents = bankOpeningBalanceText.toLongOrNull() ?: 0L
                            viewModel.saveOpeningBalances(cashCents, bankCents)
                        },
                        icon = Icons.Default.Save,
                        fullWidth = true,
                    )
                }
            }

            // CARD: TAMPILAN & TEMA (MODE GELAP)
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                    Text(
                        text = "Tampilan & Tema",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mode Gelap (Dark Mode)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Normal,
                            )
                            Text(
                                text = "Gunakan tema gelap untuk kenyamanan mata dan efisiensi baterai",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onToggleTheme() },
                        )
                    }
                }
            }

            // CARD 3: KEAMANAN & PIN
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                    Text(
                        text = "Keamanan & Akses PIN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Gunakan PIN Keamanan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Normal,
                            )
                            Text(
                                text = "Minta PIN 6-digit setiap kali membuka perbendaharaan",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Switch(
                            checked = uiState.securityPinEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.toggleSecurityPin(context, enabled)
                            },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.XS),
                        ) {
                            Text(
                                text = "Status PIN:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            StatusChip(
                                text = if (uiState.securityPinEnabled) "Aktif (6-digit PIN)" else "Tidak Aktif",
                                type = if (uiState.securityPinEnabled) ChipStatusType.SUCCESS else ChipStatusType.WARNING,
                            )
                        }

                        if (uiState.securityPinEnabled) {
                            AppOutlinedButton(
                                text = "Ubah PIN",
                                icon = Icons.Default.Lock,
                                onClick = { viewModel.openSetPinDialog() },
                            )
                        }
                    }
                }
            }

            // CARD 4: BACKUP DATA
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                    Text(
                        text = "Backup & Restore Data",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Text(
                        text = "Amankan seluruh database perbendaharaan masjid secara rutin untuk mencegah kehilangan data.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    PrimaryButton(
                        text = "Buat Backup Data Baru",
                        icon = Icons.Default.Backup,
                        onClick = { viewModel.createManualBackup(context) },
                        fullWidth = true,
                    )

                    if (uiState.backups.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(Spacing.XS))
                        Text(
                            text = "Riwayat Backup (${uiState.backups.size})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = (-0.2).sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS)) {
                            uiState.backups.take(5).forEach { backup ->
                                BackupItemRow(
                                    backup = backup,
                                    dateFormat = dateFormat,
                                    onRestore = {
                                        val file = File(backup.backupFilePath)
                                        viewModel.restoreBackupFile(context, file)
                                    },
                                )
                            }
                        }
                    }
                }
            }

            // CARD 5: RESET DATA APLIKASI
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.MD),
                    verticalArrangement = Arrangement.spacedBy(Spacing.SM),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.XS),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Peringatan",
                            tint = MaterialTheme.colorScheme.error,
                        )
                        Text(
                            text = "Reset Data Aplikasi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    Text(
                        text = "Mereset seluruh data transaksi, RAPBM, zakat, dan perbendaharaan kembali ke data awal. Tindakan ini permanen dan tidak dapat dibatalkan!",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )

                    PrimaryButton(
                        text = "Reset Seluruh Data Aplikasi",
                        icon = Icons.Default.Refresh,
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                        onClick = { viewModel.openResetConfirmDialog() },
                        fullWidth = true,
                    )
                }
            }

            // CARD 6: TENTANG APLIKASI
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToAbout,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                    ) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Tentang Aplikasi",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Normal,
                            )
                            Text(
                                text = "Versi 1.0.0 \u2022 Informasi Sistem & Lisensi",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Buka",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }

    // DIALOG 1: SET / UBAH PIN KEAMANAN
    if (uiState.showSetPinDialog) {
        SetPinDialog(
            currentPin = uiState.pinCode,
            onDismiss = { viewModel.closeSetPinDialog() },
            onSavePin = { pin ->
                viewModel.savePin(context, enabled = true, pinCode = pin)
            },
        )
    }

    // DIALOG 2: KONFIRMASI RESET DATA
    if (uiState.showResetConfirmDialog) {
        ResetConfirmDialog(
            onDismiss = { viewModel.closeResetConfirmDialog() },
            onConfirmReset = {
                viewModel.resetAllData(context)
            },
        )
    }
}

@Composable
private fun BackupItemRow(
    backup: BackupMetadataEntity,
    dateFormat: SimpleDateFormat,
    onRestore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateText = remember(backup.backupTimestamp) {
        dateFormat.format(Date(backup.backupTimestamp))
    }
    val sizeKb = backup.fileSizeInBytes / 1024

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.SM, vertical = Spacing.XS),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.2).sp,
                )
                Text(
                    text = "$sizeKb KB \u2022 Version ${backup.dbVersion}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            AppOutlinedButton(
                text = "Restore",
                icon = Icons.Default.Restore,
                onClick = onRestore,
            )
        }
    }
}

@Composable
private fun SetPinDialog(
    currentPin: String,
    onDismiss: () -> Unit,
    onSavePin: (String) -> Unit,
) {
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            tonalElevation = 0.dp,
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp)
                .padding(Spacing.MD),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.LG),
                verticalArrangement = Arrangement.spacedBy(Spacing.MD),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Atur PIN Keamanan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = (-0.2).sp,
                    )
                }

                Text(
                    text = "Masukkan 6-digit PIN keamanan untuk melindungi akses data perbendaharaan masjid.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                OutlinedTextField(
                    value = newPin,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            newPin = it
                            errorMessage = null
                        }
                    },
                    label = { Text("PIN Baru (6 Digit)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            confirmPin = it
                            errorMessage = null
                        }
                    },
                    label = { Text("Konfirmasi PIN Baru") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                ) {
                    SecondaryButton(
                        text = "Batal",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    )
                    PrimaryButton(
                        text = "Simpan PIN",
                        onClick = {
                            if (newPin.length < 4) {
                                errorMessage = "PIN minimal terdiri dari 4-6 digit angka"
                            } else if (newPin != confirmPin) {
                                errorMessage = "Konfirmasi PIN tidak cocok!"
                            } else {
                                onSavePin(newPin)
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ResetConfirmDialog(
    onDismiss: () -> Unit,
    onConfirmReset: () -> Unit,
) {
    var confirmText by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
            tonalElevation = 0.dp,
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp)
                .padding(Spacing.MD),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.LG),
                verticalArrangement = Arrangement.spacedBy(Spacing.MD),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = "Konfirmasi Reset Data",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = (-0.2).sp,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Text(
                    text = "PERINGATAN: Seluruh pencatatan kas, transaksi, RAPBM, target dana, dan zakat akan dihapus dan dikembalikan ke kondisi awal!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "Ketik 'RESET' di bawah ini untuk mengonfirmasi:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.error,
                )

                OutlinedTextField(
                    value = confirmText,
                    onValueChange = { confirmText = it },
                    placeholder = { Text("RESET") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                ) {
                    SecondaryButton(
                        text = "Batal",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    )
                    PrimaryButton(
                        text = "Reset Sekarang",
                        onClick = onConfirmReset,
                        enabled = confirmText.trim() == "RESET",
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
