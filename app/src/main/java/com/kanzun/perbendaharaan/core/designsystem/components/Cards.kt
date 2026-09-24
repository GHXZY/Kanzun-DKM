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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.kanzun.perbendaharaan.core.designsystem.Elevation
import com.kanzun.perbendaharaan.core.designsystem.IconSize
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.TypographyTokens

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = KanzunShapes.Card,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, borderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(Spacing.MD),
                content = content,
            )
        }
    } else {
        Card(
            modifier = modifier,
            shape = KanzunShapes.Card,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, borderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
            .heightIn(min = 132.dp),
        shape = KanzunShapes.HeroCard,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D47A1), // Brand Secondary Navy Anchor
            contentColor = Color.White,
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.LG, vertical = Spacing.MD + 4.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                // Micro-badge pill (Stripe style)
                Surface(
                    shape = KanzunShapes.SmallComponent,
                    color = Color.White.copy(alpha = 0.12f),
                    border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.22f)),
                ) {
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = TypographyTokens.SemiBold,
                            letterSpacing = 0.8.sp,
                        ),
                        color = Color.White.copy(alpha = 0.90f),
                        modifier = Modifier.padding(horizontal = Spacing.SM, vertical = 2.dp),
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.SM))

                // Editorial balance display with Level 1 Bold 700 and negative tracking
                Text(
                    text = amountText,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = TypographyTokens.Bold,
                        letterSpacing = (-0.64).sp,
                    ),
                    color = Color.White,
                )

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(Spacing.XS))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = TypographyTokens.Regular,
                        ),
                        color = Color.White.copy(alpha = 0.75f),
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
        modifier = modifier,
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
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = TypographyTokens.SemiBold,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(Spacing.XS))
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = TypographyTokens.Bold,
                        letterSpacing = (-0.3).sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(Spacing.XS))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = TypographyTokens.Regular,
                        ),
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
                    border = BorderStroke(0.5.dp, iconColor.copy(alpha = 0.25f)),
                    modifier = Modifier.size(36.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(IconSize.SmallAction),
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
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = TypographyTokens.SemiBold,
                ),
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
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = TypographyTokens.Bold,
                    letterSpacing = (-0.2).sp,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Target $targetAmountText",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = TypographyTokens.Regular,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(Spacing.SM))

        AppProgressBar(progress = progress, height = 6.dp)

        Spacer(modifier = Modifier.height(Spacing.XS))

        Text(
            text = "${(progress * 100).toInt()}% tercapai",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = TypographyTokens.Bold,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End),
        )
    }
}
