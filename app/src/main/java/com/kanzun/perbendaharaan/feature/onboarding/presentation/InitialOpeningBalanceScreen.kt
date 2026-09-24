package com.kanzun.perbendaharaan.feature.onboarding.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.AppTextField
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.ResponsiveContentContainer

@Composable
fun InitialOpeningBalanceScreen(
    onSaveBalances: (cashCents: Long, bankCents: Long) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var cashText by remember { mutableStateOf("") }
    var bankText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ResponsiveContentContainer {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = Spacing.LG, vertical = Spacing.XL),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.MD),
            ) {
                Spacer(modifier = Modifier.height(Spacing.MD))

                // Icon Avatar
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Text(
                    text = "Atur Saldo Awal",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.64).sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Masukkan saldo awal kas tunai dan rekening bank masjid Anda untuk memulai pencatatan keuangan yang rapi dan terukur.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Spacing.SM),
                )

                Spacer(modifier = Modifier.height(Spacing.XS))

                // Inputs Card
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(Spacing.SM),
                        verticalArrangement = Arrangement.spacedBy(Spacing.MD),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.width(Spacing.XS))
                                Text(
                                    text = "Kas Tunai Fisik",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                            Text(
                                text = "Saldo uang tunai di brankas atau pegangan bendahara masjid",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(Spacing.XS))
                            AppTextField(
                                value = cashText,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() }) cashText = input
                                },
                                label = { Text("Saldo Awal Kas Tunai (Rp)") },
                                prefix = { Text("Rp ") },
                                placeholder = { Text("0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.width(Spacing.XS))
                                Text(
                                    text = "Rekening Bank",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                            Text(
                                text = "Saldo di buku tabungan atau rekening bank syariah/operasional",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(Spacing.XS))
                            AppTextField(
                                value = bankText,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() }) bankText = input
                                },
                                label = { Text("Saldo Awal Rekening Bank (Rp)") },
                                prefix = { Text("Rp ") },
                                placeholder = { Text("0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.SM))

                PrimaryButton(
                    text = "Simpan & Lanjutkan",
                    onClick = {
                        val cashCents = cashText.toLongOrNull() ?: 0L
                        val bankCents = bankText.toLongOrNull() ?: 0L
                        onSaveBalances(cashCents, bankCents)
                    },
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    fullWidth = true,
                )

                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Lewati Langkah Ini",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = "Catatan: Anda dapat mengubah atau mengatur saldo awal ini kapan saja nanti melalui menu Pengaturan.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = Spacing.MD),
                )
            }
        }
    }
}
