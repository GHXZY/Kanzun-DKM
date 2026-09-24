package com.kanzun.perbendaharaan.feature.audit.presentation

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.ChipStatusType
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.designsystem.components.StatusChip
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportType
import com.kanzun.perbendaharaan.feature.reports.presentation.ReportsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AuditTrailScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("id", "ID"))

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = Spacing.MD)
            .padding(bottom = Spacing.XL),
        verticalArrangement = Arrangement.spacedBy(Spacing.LG),
    ) {
        Spacer(modifier = Modifier.height(Spacing.XS))

        SectionHeader(
            title = "Audit Trail Keuangan & Sistem",
            subtitle = "Riwayat aktivitas, pembuatan, pengubahan, dan penguncian data",
        )

        Button(
            onClick = {
                viewModel.selectReportType(ReportType.AUDIT_TRAIL)
                viewModel.exportPdf(context)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null)
            Spacer(modifier = Modifier.width(Spacing.XS))
            Text("Export Log Audit Trail ke PDF")
        }

        uiState.reportContent?.tableRows?.forEach { row ->
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        StatusChip(
                            text = if (row.columns.size > 3) row.columns[3] else "AKSI",
                            type = ChipStatusType.INFO,
                        )
                        Text(
                            text = if (row.columns.size > 1) row.columns[1] else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Text(
                        text = "${if (row.columns.size > 4) row.columns[4] else "Entitas"} \u2022 ID: ${if (row.columns.size > 5) row.columns[5] else "-"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = "Pengguna: ${if (row.columns.size > 2) row.columns[2] else "System"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    if (row.columns.size > 6) {
                        Text(
                            text = "Detail: ${row.columns[6]}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
