package com.kanzun.perbendaharaan.feature.dashboard.presentation.components

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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.model.FundAllocation
import com.kanzun.perbendaharaan.core.model.FundAllocationItem
import kotlin.math.atan2

val FundPieChartColors = listOf(
    Color(0xFF0D47A1), // Primary Blue
    Color(0xFF2E6F3B), // Emerald Green
    Color(0xFFF9B637), // Amber Accent
    Color(0xFF4D83C5), // Light Primary Blue
    Color(0xFF438650), // Mint Secondary
    Color(0xFF7B8794), // Neutral Grey for Lainnya
)

@Composable
fun FundAllocationPieChart(
    allocation: FundAllocation,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalCents = allocation.totalAllocated.amountInCents
    var selectedItem by remember { mutableStateOf<FundAllocationItem?>(null) }

    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onCardClick,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.MD),
        ) {
            SectionHeader(
                title = "Alokasi Dana",
            )

            if (totalCents <= 0 || allocation.items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Belum ada alokasi dana",
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
                    // Donut Canvas
                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        val fallbackColor = MaterialTheme.colorScheme.primary

                        Canvas(
                            modifier = Modifier
                                .size(130.dp)
                                .pointerInput(allocation.items) {
                                    detectTapGestures { tapOffset ->
                                        val center = Offset(size.width / 2f, size.height / 2f)
                                        val dx = tapOffset.x - center.x
                                        val dy = tapOffset.y - center.y
                                        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                        if (angle < 0) angle += 360f

                                        val normalizedAngle = (angle + 90f) % 360f
                                        var currentStart = 0f

                                        for (item in allocation.items) {
                                            val sweep = (item.percentage / 100f) * 360f
                                            if (normalizedAngle >= currentStart && normalizedAngle <= currentStart + sweep) {
                                                selectedItem = item
                                                break
                                            }
                                            currentStart += sweep
                                        }
                                    }
                                },
                        ) {
                            val strokeWidth = 24.dp.toPx()
                            val chartSize = size.width - strokeWidth
                            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

                            var currentStartAngle = -90f

                            allocation.items.forEachIndexed { index, item ->
                                val sweepAngle = (item.percentage / 100f) * 360f
                                val color = FundPieChartColors.getOrElse(index) { fallbackColor }
                                val isSelected = selectedItem?.fundId == item.fundId

                                if (sweepAngle > 0f) {
                                    drawArc(
                                        color = color,
                                        startAngle = currentStartAngle,
                                        sweepAngle = sweepAngle,
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = Size(chartSize, chartSize),
                                        style = Stroke(width = if (isSelected) strokeWidth + 6.dp.toPx() else strokeWidth),
                                    )
                                }
                                currentStartAngle += sweepAngle
                            }
                        }


                        // Center Label
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "Alokasi",
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

                    // Legend Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.XS),
                    ) {
                        allocation.items.forEachIndexed { index, item ->
                            val color = FundPieChartColors.getOrElse(index) { MaterialTheme.colorScheme.primary }
                            val isSelected = selectedItem?.fundId == item.fundId

                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                    Text(
                                        text = item.fundName,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }

                                Text(
                                    text = String.format("%.1f%%", item.percentage),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                // Segment Detail Interactive Display
                selectedItem?.let { item ->
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
                                Text(
                                    text = item.fundName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "${String.format("%.1f%%", item.percentage)} dari total alokasi dana",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Text(
                                text = item.allocatedAmount.formatRupiah(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
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
