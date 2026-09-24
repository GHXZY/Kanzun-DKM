package com.kanzun.perbendaharaan.core.designsystem.components

import com.kanzun.perbendaharaan.core.designsystem.neomorphic
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.kanzun.perbendaharaan.core.designsystem.Elevation
import com.kanzun.perbendaharaan.core.designsystem.IconSize
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.neomorphic(KanzunShapes.Card),
            shape = KanzunShapes.Card,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(0.5.dp, borderColor.copy(alpha = 0.18f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(
                modifier = Modifier.padding(Spacing.MD),
                content = content,
            )
        }
    } else {
        Card(
            modifier = modifier.neomorphic(KanzunShapes.Card),
            shape = KanzunShapes.Card,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(0.5.dp, borderColor.copy(alpha = 0.18f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(
                modifier = Modifier.padding(Spacing.MD),
                content = content,
            )
        }
    }
}

@Composable
fun HeroCard(
    title: String,
    amountText: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp)
            .neomorphic(KanzunShapes.HeroCard),
        shape = KanzunShapes.HeroCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp)
                .padding(horizontal = Spacing.LG, vertical = Spacing.LG),
            contentAlignment = Alignment.CenterStart,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.80f),
                    fontWeight = FontWeight.Medium,
                )

                Spacer(modifier = Modifier.height(Spacing.SM))

                Text(
                    text = amountText,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(Spacing.XS))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.70f),
                    )
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    valueText: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    iconColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    onClick: (() -> Unit)? = null,
) {
    AppCard(
        modifier = modifier.neomorphic(KanzunShapes.Card),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(Spacing.XS))
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(Spacing.XS))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (icon != null) {
                Spacer(modifier = Modifier.width(Spacing.SM))
                Surface(
                    shape = KanzunShapes.SmallComponent,
                    color = iconContainerColor,
                    contentColor = iconColor,
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(IconSize.Standard),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressCard(
    title: String,
    currentAmountText: String,
    targetAmountText: String,
    progress: Float, // 0.0f to 1.0f
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    onClick: (() -> Unit)? = null,
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (badgeText != null) {
                StatusChip(text = badgeText, type = ChipStatusType.INFO)
            }
        }

        Spacer(modifier = Modifier.height(Spacing.MDS))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.XS),
        ) {
            Text(
                text = currentAmountText,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Target $targetAmountText",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(Spacing.SM))

        AppProgressBar(progress = progress)

        Spacer(modifier = Modifier.height(Spacing.XS))

        Text(
            text = "${(progress * 100).toInt()}% tercapai",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End),
        )
    }
}
