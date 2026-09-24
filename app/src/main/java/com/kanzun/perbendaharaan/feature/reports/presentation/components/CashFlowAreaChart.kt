package com.kanzun.perbendaharaan.feature.reports.presentation.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.KpiCard
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.model.Money

data class CashFlowDataPoint(
    val dateLabel: String,
    val incomeInCents: Long,
    val expenseInCents: Long,
) {
    val netInCents: Long get() = incomeInCents - expenseInCents
}

@Composable
fun CashFlowAreaChart(
    dataPoints: List<CashFlowDataPoint>,
    modifier: Modifier = Modifier,
) {
    val totalIncomeCents = dataPoints.sumOf { it.incomeInCents }
    val totalExpenseCents = dataPoints.sumOf { it.expenseInCents }
    val netCashFlowCents = totalIncomeCents - totalExpenseCents

    val primaryColor = MaterialTheme.colorScheme.primary // #0D47A1
    val accentColor = Color(0xFFF9B637) // Amber Accent #F9B637
    val gridLineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)

    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.MD),
        ) {
            SectionHeader(
                title = "Arus Kas Pemasukan & Pengeluaran",
            )

            // KPI Summary Row Above Area Chart
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                ) {
                    KpiCard(
                        title = "Total Pemasukan",
                        valueText = Money(totalIncomeCents).formatted,
                        modifier = Modifier.weight(1f),
                    )
                    KpiCard(
                        title = "Total Pengeluaran",
                        valueText = Money(totalExpenseCents).formatted,
                        modifier = Modifier.weight(1f),
                    )
                }

                KpiCard(
                    title = "ARUS KAS BERSIH (NET CASH FLOW)",
                    valueText = Money(netCashFlowCents).formatted,
                    subtitle = if (netCashFlowCents >= 0) "Surplus Kas" else "Defisit Kas",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (dataPoints.isEmpty() || (totalIncomeCents == 0L && totalExpenseCents == 0L)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Belum ada transaksi pada periode ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                val maxVal = dataPoints.maxOfOrNull { maxOf(it.incomeInCents, it.expenseInCents) }
                    ?.coerceAtLeast(100_000_00L) ?: 100_000_00L

                val indicatorColor = MaterialTheme.colorScheme.onSurface

                // Area Chart Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .pointerInput(dataPoints) {
                                detectTapGestures { tapOffset ->
                                    val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                                    val index = ((tapOffset.x + (stepX / 2f)) / stepX).toInt().coerceIn(0, dataPoints.size - 1)
                                    selectedPointIndex = index
                                }
                            },
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Grid lines
                        val steps = 3
                        for (i in 0..steps) {
                            val y = canvasHeight * (1f - i.toFloat() / steps)
                            drawLine(
                                color = gridLineColor,
                                start = Offset(0f, y),
                                end = Offset(canvasWidth, y),
                                strokeWidth = 1.dp.toPx(),
                            )
                        }

                        val stepX = canvasWidth / (dataPoints.size - 1).coerceAtLeast(1)

                        // Income Path & Area Fill
                        val incomePath = Path()
                        val incomeFillPath = Path()

                        dataPoints.forEachIndexed { i, pt ->
                            val x = i * stepX
                            val y = canvasHeight * (1f - (pt.incomeInCents.toFloat() / maxVal))
                            if (i == 0) {
                                incomePath.moveTo(x, y)
                                incomeFillPath.moveTo(x, canvasHeight)
                                incomeFillPath.lineTo(x, y)
                            } else {
                                incomePath.lineTo(x, y)
                                incomeFillPath.lineTo(x, y)
                            }
                        }
                        incomeFillPath.lineTo(canvasWidth, canvasHeight)
                        incomeFillPath.close()

                        // Draw Income Area Fill
                        drawPath(
                            path = incomeFillPath,
                            color = primaryColor.copy(alpha = 0.15f),
                        )
                        // Draw Income Stroke Line
                        drawPath(
                            path = incomePath,
                            color = primaryColor,
                            style = Stroke(width = 3.dp.toPx()),
                        )

                        // Expense Path & Area Fill
                        val expensePath = Path()
                        val expenseFillPath = Path()

                        dataPoints.forEachIndexed { i, pt ->
                            val x = i * stepX
                            val y = canvasHeight * (1f - (pt.expenseInCents.toFloat() / maxVal))
                            if (i == 0) {
                                expensePath.moveTo(x, y)
                                expenseFillPath.moveTo(x, canvasHeight)
                                expenseFillPath.lineTo(x, y)
                            } else {
                                expensePath.lineTo(x, y)
                                expenseFillPath.lineTo(x, y)
                            }
                        }
                        expenseFillPath.lineTo(canvasWidth, canvasHeight)
                        expenseFillPath.close()

                        // Draw Expense Area Fill
                        drawPath(
                            path = expenseFillPath,
                            color = accentColor.copy(alpha = 0.20f),
                        )
                        // Draw Expense Stroke Line
                        drawPath(
                            path = expensePath,
                            color = accentColor,
                            style = Stroke(width = 3.dp.toPx()),
                        )

                        // Draw selected point indicator line
                        selectedPointIndex?.let { idx ->
                            val selectedX = idx * stepX
                            drawLine(
                                color = indicatorColor,
                                start = Offset(selectedX, 0f),
                                end = Offset(selectedX, canvasHeight),
                                strokeWidth = 1.dp.toPx(),
                            )
                        }
                    }
                }


                // Legend Below Chart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.MD),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ChartLegendItem(color = primaryColor, label = "Pemasukan")
                        ChartLegendItem(color = accentColor, label = "Pengeluaran")
                    }

                    Text(
                        text = "${dataPoints.firstOrNull()?.dateLabel ?: ""} - ${dataPoints.lastOrNull()?.dateLabel ?: ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Interactive Tooltip Box
                selectedPointIndex?.let { idx ->
                    val point = dataPoints.getOrNull(idx)
                    if (point != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Spacing.XS),
                            shape = KanzunShapes.Card,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.MD),
                                verticalArrangement = Arrangement.spacedBy(Spacing.XS),
                            ) {
                                Text(
                                    text = "Periode: ${point.dateLabel}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text("Pemasukan:", style = MaterialTheme.typography.bodySmall, color = primaryColor)
                                    Text(Money(point.incomeInCents).formatted, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text("Pengeluaran:", style = MaterialTheme.typography.bodySmall, color = accentColor)
                                    Text(Money(point.expenseInCents).formatted, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text("Arus Kas Bersih (Net):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = Money(point.netInCents).formatted,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (point.netInCents >= 0) primaryColor else MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartLegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.XS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(10.dp),
            shape = KanzunShapes.Pill,
            color = color,
        ) {}
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
