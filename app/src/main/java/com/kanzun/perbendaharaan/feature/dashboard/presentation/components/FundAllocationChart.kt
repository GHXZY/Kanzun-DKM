package com.kanzun.perbendaharaan.feature.dashboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.SectionHeader
import com.kanzun.perbendaharaan.core.model.FundAllocation

val FundChartColors = listOf(
    Color(0xFF0D47A1), // Primary Blue
    Color(0xFF2E6F3B), // Emerald Green
    Color(0xFFF9B637), // Amber Accent
    Color(0xFF4D83C5), // Light Primary Blue
    Color(0xFF438650), // Mint Secondary
    Color(0xFF7B8794), // Neutral Grey for Lainnya
)

@Composable
fun FundAllocationChart(
    allocation: FundAllocation,
    onChartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onChartClick,
    ) {
        SectionHeader(
            title = "Alokasi Peruntukan Dana",
            subtitle = "Distribusi saldo kas berdasarkan Dana/Fund",
        )

        Spacer(modifier = Modifier.height(Spacing.MD))

        // Stacked Horizontal Bar Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(KanzunShapes.Pill)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                allocation.items.forEachIndexed { index, item ->
                    val color = FundChartColors.getOrElse(index) { MaterialTheme.colorScheme.primary }
                    val fraction = (item.percentage / 100f).coerceIn(0f, 1f)
                    if (fraction > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(fraction.coerceAtLeast(0.01f))
                                .fillMaxHeight()
                                .background(color),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.MD))

        // Legend Items
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.SM)) {
            allocation.items.forEachIndexed { index, item ->
                val color = FundChartColors.getOrElse(index) { MaterialTheme.colorScheme.primary }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(12.dp),
                            shape = KanzunShapes.Pill,
                            color = color,
                        ) {}

                        Spacer(modifier = Modifier.width(Spacing.SM))

                        Text(
                            text = item.fundName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.allocatedAmount.formatRupiah(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.width(Spacing.SM))
                        Text(
                            text = "${item.percentage.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
