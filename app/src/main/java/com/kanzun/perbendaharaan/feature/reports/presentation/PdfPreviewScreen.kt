package com.kanzun.perbendaharaan.feature.reports.presentation

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.ResponsiveContentContainer
import com.kanzun.perbendaharaan.feature.reports.domain.model.ReportContent
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfPreviewScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.generatedPdfFile) {
        val pdf = uiState.generatedPdfFile
        if (pdf != null) {
            Toast.makeText(context, "Dokumen PDF Berhasil Dibuat: ${pdf.name}", Toast.LENGTH_LONG).show()
            viewModel.clearPdfFile()
        }
    }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 0.dp,
                shadowElevation = 1.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.MD),
                ) {
                    PrimaryButton(
                        text = if (uiState.isGeneratingPdf) "Mengunduh PDF..." else "Download PDF",
                        icon = Icons.Default.Download,
                        onClick = { viewModel.exportPdf(context) },
                        fullWidth = true,
                    )
                }
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        ResponsiveContentContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(Spacing.MD),
                verticalArrangement = Arrangement.spacedBy(Spacing.MD),
            ) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    uiState.reportContent?.let { content ->
                        FullSuratLaporanDocument(reportContent = content)
                    } ?: run {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Tidak ada konten laporan untuk ditampilkan.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullSuratLaporanDocument(
    reportContent: ReportContent,
    modifier: Modifier = Modifier,
) {
    val logoFile = if (reportContent.logoPath.isNotBlank()) File(reportContent.logoPath) else null
    val logoBitmap = remember(reportContent.logoPath) {
        if (logoFile != null && logoFile.exists()) {
            BitmapFactory.decodeFile(logoFile.absolutePath)?.asImageBitmap()
        } else null
    }

    val todayFormatted = remember { SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date()) }
    val cityLocation = remember(reportContent.mosqueAddress) {
        reportContent.mosqueAddress.split(",").lastOrNull()?.trim()?.ifBlank { "Jakarta" } ?: "Jakarta"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFE5EDF5)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.LG),
            verticalArrangement = Arrangement.spacedBy(Spacing.MD),
        ) {
            // 1. KOP SURAT
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.MD),
            ) {
                if (logoBitmap != null) {
                    Image(
                        bitmap = logoBitmap,
                        contentDescription = "Logo Masjid",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(4.dp)),
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE5EDF5)),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = "Logo Fallback",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reportContent.mosqueName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.4).sp,
                        color = Color(0xFF0F172A),
                    )
                    Text(
                        text = reportContent.mosqueAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF475569),
                    )
                    Text(
                        text = "Telp: ${reportContent.mosquePhone} \u2022 Email: ${reportContent.mosqueEmail}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF64748B),
                    )
                }
            }

            // Garis Pembatas Kop Surat
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF0F172A)),
            )

            Spacer(modifier = Modifier.height(Spacing.XS))

            // 2. JUDUL SURAT & PERIODE
            Text(
                text = "SURAT LAPORAN ${reportContent.reportType.title.uppercase()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp,
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Periode: ${reportContent.periodLabel}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF475569),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(Spacing.XS))

            // 3. RINGKASAN KEUANGAN SURAT
            if (reportContent.summaries.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE5EDF5)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.MD),
                        verticalArrangement = Arrangement.spacedBy(Spacing.XS),
                    ) {
                        Text(
                            text = "Ringkasan Eksekutif",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp,
                            color = Color(0xFF0F172A),
                        )
                        reportContent.summaries.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF334155),
                                )
                                Text(
                                    text = item.value,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.2).sp,
                                    color = Color(0xFF0F172A),
                                )
                            }
                        }
                    }
                }
            }

            // 4. TABEL DOKUMEN LAPORAN LENGKAP
            if (reportContent.tableHeaders.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFFE5EDF5), RoundedCornerShape(4.dp)),
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A))
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                    ) {
                        reportContent.tableHeaders.forEach { header ->
                            Text(
                                text = header,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    // Table Rows
                    reportContent.tableRows.forEachIndexed { index, row ->
                        val bg = if (index % 2 == 1) Color(0xFFF8FAFC) else Color.White
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bg)
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                        ) {
                            row.columns.forEach { text ->
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Normal,
                                    letterSpacing = (-0.2).sp,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.SM))

            // 5. PENUTUP
            Text(
                text = "Demikian surat laporan ini dibuat dengan sebenarnya sebagai bentuk pertanggungjawaban dan transparansi pengelolaan perbendaharaan masjid.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF475569),
                fontStyle = FontStyle.Italic,
            )

            Spacer(modifier = Modifier.height(Spacing.MD))

            // 6. TANDA TANGAN DKM & BENDAHARA
            Text(
                text = "$cityLocation, $todayFormatted",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF475569),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )

            Spacer(modifier = Modifier.height(Spacing.SM))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Ketua DKM Masjid",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569),
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Text(
                        text = reportContent.chairmanName.ifBlank { "H. Ahmad Dahlan" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF0F172A),
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Bendahara Masjid",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569),
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Text(
                        text = reportContent.treasurerName.ifBlank { "H. Muhammad Hatta" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF0F172A),
                    )
                }
            }
        }
    }
}
