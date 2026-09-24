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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.kanzun.perbendaharaan.core.designsystem.Elevation
import com.kanzun.perbendaharaan.core.designsystem.IconSize
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import java.io.File

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
    badgeText: String? = null,
    illustrationPath: String? = "C:/Dev/Kanzun/Masjid.svg",
    actionButton: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }

    val imageModel: Any = remember(illustrationPath) {
        if (illustrationPath != null && File(illustrationPath).exists()) {
            File(illustrationPath)
        } else {
            "file:///android_asset/masjid.svg"
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .neomorphic(KanzunShapes.HeroCard),
        shape = KanzunShapes.HeroCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.LG),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.XS),
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium,
                        )
                        if (badgeText != null) {
                            StatusChip(
                                text = badgeText,
                                type = ChipStatusType.SUCCESS,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.XS))

                    Text(
                        text = amountText,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )

                    if (subtitle != null) {
                        Spacer(modifier = Modifier.height(Spacing.XS))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                        )
                    }

                    if (actionButton != null) {
                        Spacer(modifier = Modifier.height(Spacing.MD))
                        actionButton()
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.SM))

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    imageLoader = imageLoader,
                    contentDescription = "Mosque Illustration",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(width = 125.dp, height = 110.dp)
                )
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
