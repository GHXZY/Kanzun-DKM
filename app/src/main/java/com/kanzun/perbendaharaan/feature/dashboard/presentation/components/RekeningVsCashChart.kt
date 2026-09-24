package com.kanzun.perbendaharaan.feature.dashboard.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.model.CashBreakdown
import kotlin.math.atan2

@Composable
fun RekeningVsCashChart(
    breakdown: CashBreakdown,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalCents = breakdown.totalCash.amountInCents
    val bankCents = breakdown.bankTotal.amountInCents
    val cashCents = breakdown.cashTotal.amountInCents

    val bankPct = if (totalCents > 0) (bankCents.toDouble() / totalCents.toDouble() * 100).toFloat() else 0f
    val cashPct = if (totalCents > 0) (cashCents.toDouble() / totalCents.toDouble() * 100).toFloat() else 0f

    val bankColor = Color(0xFF0D47A1) // Primary Blue
    val cashColor = Color(0xFFF9B637) // Amber Accent

    var selectedSegment by remember { mutableStateOf<String?>(null) } // "Bank" or "Cash"

    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onCardClick,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.MD),
        ) {
            SectionHeader(
                title = "Rekening vs Cash",
            )

            if (totalCents <= 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Belum ada data kas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Interactive Canvas Donut Chart
                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(130.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures { tapOffset ->
                                        val center = Offset(size.width / 2f, size.height / 2f)
                                        val dx = tapOffset.x - center.x
                                        val dy = tapOffset.y - center.y
                                        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                        if (angle < 0) angle += 360f

                                        // Bank segment starts at -90 deg (270 deg)
                                        val bankAngle = (bankPct / 100f) * 360f
                                        val normalizedAngle = (angle + 90f) % 360f

                                        selectedSegment = if (normalizedAngle <= bankAngle) "Bank" else "Cash"
                                    }
                                },
                        ) {
                            val strokeWidth = 24.dp.toPx()
                            val chartSize = size.width - strokeWidth
                            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

                            val bankSweep = (bankPct / 100f) * 360f
                            val cashSweep = 360f - bankSweep

                            // Draw Bank Arc
                            if (bankSweep > 0f) {
                                drawArc(
                                    color = bankColor,
                                    startAngle = -90f,
                                    sweepAngle = bankSweep,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = Size(chartSize, chartSize),
                                    style = Stroke(width = if (selectedSegment == "Bank") strokeWidth + 6.dp.toPx() else strokeWidth),
                                )
                            }

                            // Draw Cash Arc
                            if (cashSweep > 0f) {
                                drawArc(
                                    color = cashColor,
                                    startAngle = -90f + bankSweep,
                                    sweepAngle = cashSweep,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = Size(chartSize, chartSize),
                                    style = Stroke(width = if (selectedSegment == "Cash") strokeWidth + 6.dp.toPx() else strokeWidth),
                                )
                            }
                        }

                        // Center Label
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "Total Kas",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = formatShortRupiah(totalCents),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(Spacing.MD))

                    // Legend Box
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.SM),
                    ) {
                        // Rekening Row
                        LegendRow(
                            label = "Rekening (Bank)",
                            amountText = breakdown.bankTotal.formatRupiah(),
                            percentageText = String.format("%.1f%%", bankPct),
                            color = bankColor,
                            isSelected = selectedSegment == "Bank",
                            onClick = { selectedSegment = if (selectedSegment == "Bank") null else "Bank" },
                        )

                        // Cash Row
                        LegendRow(
                            label = "Cash (Kas Tunai)",
                            amountText = breakdown.cashTotal.formatRupiah(),
                            percentageText = String.format("%.1f%%", cashPct),
                            color = cashColor,
                            isSelected = selectedSegment == "Cash",
                            onClick = { selectedSegment = if (selectedSegment == "Cash") null else "Cash" },
                        )
                    }
                }

                // Interactive Detail Popup if segment selected
                selectedSegment?.let { segment ->
                    val name = if (segment == "Bank") "Rekening (Bank)" else "Kas Tunai (Physical Cash)"
                    val amount = if (segment == "Bank") breakdown.bankTotal.formatRupiah() else breakdown.cashTotal.formatRupiah()
                    val pct = if (segment == "Bank") String.format("%.1f%%", bankPct) else String.format("%.1f%%", cashPct)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.SM),
                        shape = KanzunShapes.Card,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.MD),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(text = name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = "$pct dari total kas masjid", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(text = amount, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendRow(
    label: String,
    amountText: String,
    percentageText: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(10.dp),
                shape = KanzunShapes.Pill,
                color = color,
            ) {}
            Spacer(modifier = Modifier.width(Spacing.XS))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = amountText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        Text(
            text = percentageText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun formatShortRupiah(amountInCents: Long): String {
    val rupiah = amountInCents / 100
    return when {
        rupiah >= 1_000_000_000 -> String.format("%.1fB", rupiah / 1_000_000_000f)
        rupiah >= 1_000_000 -> String.format("%.1fM", rupiah / 1_000_000f)
        rupiah >= 1_000 -> String.format("%.0fK", rupiah / 1_000f)
        else -> "Rp $rupiah"
    }
}
