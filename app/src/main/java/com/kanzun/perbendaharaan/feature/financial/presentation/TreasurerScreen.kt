package com.kanzun.perbendaharaan.feature.financial.presentation

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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.KpiCard
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader

import com.kanzun.perbendaharaan.core.designsystem.components.ResponsiveContentContainer

@Composable
fun TreasurerScreen(
    onNavigateToFeature: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
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

            SectionHeader(
                title = "Bendahara",
            )

            // Module Feature Cards
            KpiCard(
                title = "Kelola Arus Kas",
                valueText = "Arus Kas",
                icon = Icons.Default.ReceiptLong,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = { onNavigateToFeature("cash_flow") },
            )

            KpiCard(
                title = "Inventaris & Sarana",
                valueText = "Aset Masjid",
                icon = Icons.Default.Category,
                iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = { onNavigateToFeature("assets") },
            )

            KpiCard(
                title = "Penerimaan & Distribution",
                valueText = "Zakat & Mustahik",
                icon = Icons.Default.CleanHands,
                iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                iconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = { onNavigateToFeature("zakat") },
            )

            KpiCard(
                title = "Penggalangan Dana",
                valueText = "Target Dana",
                icon = Icons.Default.VolunteerActivism,
                iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = { onNavigateToFeature("fundraising") },
            )

            KpiCard(
                title = "Perencanaan Anggaran",
                valueText = "RAPBM",
                icon = Icons.Default.Calculate,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = { onNavigateToFeature("budget") },
            )
        }
    }
}
