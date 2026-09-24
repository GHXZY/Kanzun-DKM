package com.kanzun.perbendaharaan.core.designsystem.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import com.kanzun.perbendaharaan.core.designsystem.neomorphic
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kanzun.perbendaharaan.core.designsystem.IconSize
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing

enum class ChipStatusType {
    SUCCESS,
    WARNING,
    ERROR,
    INFO,
    NEUTRAL,
}

@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier,
    type: ChipStatusType = ChipStatusType.INFO,
    icon: ImageVector? = null,
) {
    val (backgroundColor, textColor) = when (type) {
        ChipStatusType.SUCCESS -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        ChipStatusType.WARNING -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        ChipStatusType.ERROR -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        ChipStatusType.INFO -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        ChipStatusType.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(KanzunShapes.Pill)
            .background(backgroundColor)
            .padding(horizontal = Spacing.MD, vertical = Spacing.XS + 2.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(IconSize.Tiny),
                )
                Spacer(modifier = Modifier.width(Spacing.XS))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
            )
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp).semantics { this.selected = selected }.neomorphic(KanzunShapes.Pill, inset = selected),
        shape = KanzunShapes.Pill,
        color = backgroundColor,
        contentColor = textColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.MD, vertical = Spacing.SM + 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.Inline),
                )
                Spacer(modifier = Modifier.width(Spacing.XS))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
