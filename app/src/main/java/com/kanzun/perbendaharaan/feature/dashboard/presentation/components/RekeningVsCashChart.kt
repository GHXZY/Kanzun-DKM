package com.kanzun.perbendaharaan.feature.dashboard.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.draw.clip
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
                            val strokeWidth = 18.dp.toPx()
                            val chartSize = size.width - strokeWidth
                            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

                            val bankSweep = (bankPct / 100f) * 360f
                            val cashSweep = 360f - bankSweep

                            // Background subtle guide track
                            drawArc(
                                color = Color(0xFFF1F5F9),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = Size(chartSize, chartSize),
                                style = Stroke(width = strokeWidth),
                            )

                            // Draw Bank Arc
                            if (bankSweep > 0f) {
                                drawArc(
                                    color = bankColor,
                                    startAngle = -90f,
                                    sweepAngle = bankSweep,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = Size(chartSize, chartSize),
                                    style = Stroke(width = if (selectedSegment == "Bank") strokeWidth + 4.dp.toPx() else strokeWidth),
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
                                    style = Stroke(width = if (selectedSegment == "Cash") strokeWidth + 4.dp.toPx() else strokeWidth),
                                )
                            }
                        }

                        // Center Label
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "TOTAL KAS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Normal,
                                    letterSpacing = 0.6.sp,
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatShortRupiah(totalCents),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = (-0.3).sp,
                                ),
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
                            .padding(top = Spacing.XS),
                        shape = KanzunShapes.Card,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.MD),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                                )
                                Text(
                                    text = "$pct dari total kas masjid",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Text(
                                text = amount,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Light),
                                color = MaterialTheme.colorScheme.primary,
                            )
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
        modifier = modifier
            .fillMaxWidth()
            .clip(KanzunShapes.SmallComponent)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Surface(
                modifier = Modifier.size(8.dp),
                shape = KanzunShapes.SmallComponent,
                color = color,
            ) {}
            Spacer(modifier = Modifier.width(Spacing.SM))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = amountText,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        Surface(
            shape = KanzunShapes.SmallComponent,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Text(
                text = percentageText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
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
