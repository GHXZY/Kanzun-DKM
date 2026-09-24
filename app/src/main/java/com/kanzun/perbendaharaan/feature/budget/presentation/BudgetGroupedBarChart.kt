package com.kanzun.perbendaharaan.feature.budget.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard

@Composable
fun BudgetGroupedBarChart(
    title: String,
    items: List<CategoryBudgetVsActual>,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val primaryColor = MaterialTheme.colorScheme.primary
    val actualColor = MaterialTheme.colorScheme.secondary
    val gridLineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val textStyle = MaterialTheme.typography.labelSmall

    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.MD),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.MD),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LegendItem(color = primaryColor, label = "Rencana")
                    LegendItem(color = actualColor, label = "Realisasi")
                }
            }

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Belum ada data anggaran",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                val maxVal = items.maxOfOrNull { maxOf(it.plannedAmountInCents, it.actualAmountInCents) }
                    ?.coerceAtLeast(100_000_00L) ?: 100_000_00L

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                ) {
                    val groupWidthPx = 72.dp
                    val chartHeight = 180.dp
                    val totalChartWidth = (items.size * 72).dp.coerceAtLeast(320.dp)

                    Canvas(
                        modifier = Modifier
                            .width(totalChartWidth)
                            .height(chartHeight)
                            .padding(top = Spacing.SM, bottom = Spacing.LG),
                    ) {
                        val canvasHeight = size.height
                        val canvasWidth = size.width

                        // Draw background grid lines
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

                        val barWidth = 14.dp.toPx()
                        val barSpacing = 4.dp.toPx()
                        val groupStep = canvasWidth / items.size

                        items.forEachIndexed { index, item ->
                            val centerX = groupStep * index + groupStep / 2f

                            val plannedHeight = (item.plannedAmountInCents.toFloat() / maxVal) * (canvasHeight - 20.dp.toPx())
                            val actualHeight = (item.actualAmountInCents.toFloat() / maxVal) * (canvasHeight - 20.dp.toPx())

                            val plannedLeft = centerX - barWidth - (barSpacing / 2f)
                            val actualLeft = centerX + (barSpacing / 2f)

                            // Planned Bar
                            if (plannedHeight > 0f) {
                                drawRoundRect(
                                    color = primaryColor,
                                    topLeft = Offset(plannedLeft, canvasHeight - plannedHeight),
                                    size = Size(barWidth, plannedHeight),
                                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                                )
                            }

                            // Actual Bar
                            if (actualHeight > 0f) {
                                drawRoundRect(
                                    color = actualColor,
                                    topLeft = Offset(actualLeft, canvasHeight - actualHeight),
                                    size = Size(barWidth, actualHeight),
                                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                                )
                            }
                        }
                    }
                }

                // Category labels under chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    val totalChartWidth = (items.size * 72).dp.coerceAtLeast(320.dp)
                    val stepDp = totalChartWidth / items.size

                    items.forEach { item ->
                        Box(
                            modifier = Modifier
                                .width(stepDp)
                                .padding(horizontal = 2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = item.categoryName,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
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
            shape = CircleShape,
            color = color,
        ) {}
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
