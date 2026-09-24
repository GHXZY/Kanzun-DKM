package com.kanzun.perbendaharaan.feature.notifications.presentation

import androidx.compose.foundation.layout.heightIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrackChanges
import com.kanzun.perbendaharaan.core.designsystem.components.AppAlertDialog as AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzun.perbendaharaan.core.designsystem.Elevation
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.ResponsiveContentContainer
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationCategory
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationDateGroup
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationFilter
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationItem
import com.kanzun.perbendaharaan.feature.notifications.domain.model.NotificationPriority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val WarningAccentColor = Color(0xFFF9B637)

@Composable
fun NotificationScreen(
    onNavigateToFeature: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val activeFilter by viewModel.activeFilter.collectAsState()
    val dateGroups by viewModel.dateGroupsState.collectAsState()
    val unreadCount by viewModel.unreadCountState.collectAsState()
    val missingEntityMessage by viewModel.missingEntityDialogMessage.collectAsState()

    if (missingEntityMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMissingEntityDialog() },
            title = { Text("Informasi Data", fontWeight = FontWeight.Bold) },
            text = { Text(missingEntityMessage ?: "Data terkait tidak tersedia.") },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissMissingEntityDialog() }) {
                    Text("Mengerti")
                }
            },
        )
    }

    ResponsiveContentContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
        // Filter chips horizontal row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.SM),
            contentPadding = PaddingValues(horizontal = Spacing.MD),
            horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
        ) {
            items(NotificationFilter.entries.toTypedArray()) { filter ->
                val isSelected = activeFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setFilter(filter) },
                    label = {
                        Text(
                            text = filter.label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    shape = KanzunShapes.Pill,
                )
            }
        }

        // Header status and "Semua sudah dibaca" action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.MD, vertical = Spacing.XS),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (unreadCount > 0) "$unreadCount belum dibaca" else "Semua telah dibaca",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (unreadCount > 0) {
                TextButton(
                    onClick = { viewModel.markAllAsRead() },
                    contentPadding = PaddingValues(horizontal = Spacing.SM, vertical = 0.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Mark all read",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(Spacing.XS))
                    Text(
                        text = "Semua sudah dibaca",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        // Main List or Empty State
        if (dateGroups.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.LG),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = KanzunShapes.Pill,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "Kosong",
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.MD))
                    Text(
                        text = "Belum ada notifikasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(Spacing.XS))
                    Text(
                        text = "Informasi penting tentang keuangan dan aktivitas masjid akan muncul di sini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = Spacing.MD),
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Spacing.MD, vertical = Spacing.SM),
                verticalArrangement = Arrangement.spacedBy(Spacing.MD),
            ) {
                dateGroups.forEach { group ->
                    item(key = group.groupTitle) {
                        Text(
                            text = group.groupTitle,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = Spacing.XS),
                        )
                    }

                    items(group.items, key = { it.id }) { item ->
                        NotificationItemCard(
                            item = item,
                            onClick = {
                                viewModel.onNotificationClicked(
                                    item = item,
                                    onNavigate = onNavigateToFeature,
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
fun NotificationItemCard(
    item: NotificationItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unreadBackground = if (!item.isRead) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val priorityBorder = when (item.priority) {
        NotificationPriority.IMPORTANT -> MaterialTheme.colorScheme.error
        NotificationPriority.WARNING -> WarningAccentColor
        NotificationPriority.INFO -> Color.Transparent
    }

    val iconVector = getCategoryIcon(item.category)
    val iconTint = when (item.priority) {
        NotificationPriority.IMPORTANT -> MaterialTheme.colorScheme.error
        NotificationPriority.WARNING -> WarningAccentColor
        NotificationPriority.INFO -> MaterialTheme.colorScheme.primary
    }

    val formattedTime = formatTimestamp(item.timestamp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(KanzunShapes.Card)
            .clickable { onClick() }
            .then(
                if (item.priority != NotificationPriority.INFO) {
                    Modifier.border(
                        width = 1.dp,
                        color = priorityBorder.copy(alpha = 0.6f),
                        shape = KanzunShapes.Card,
                    )
                } else Modifier
            ),
        color = unreadBackground,
        tonalElevation = if (!item.isRead) Elevation.Level1 else Elevation.Level0,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.MD),
            verticalAlignment = Alignment.Top,
        ) {
            // Category Icon Badge
            Surface(
                modifier = Modifier.size(42.dp),
                shape = KanzunShapes.SmallComponent,
                color = iconTint.copy(alpha = 0.12f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = item.category.label,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.MD))

            // Main Content Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                    ) {
                        if (!item.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(KanzunShapes.Pill)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(Spacing.XS))
                        }
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.XS))

                Text(
                    text = item.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(Spacing.SM))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 11.sp,
                    )

                    if (item.targetEntityType != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Lihat detail",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.width(Spacing.XS))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Go",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getCategoryIcon(category: NotificationCategory): ImageVector {
    return when (category) {
        NotificationCategory.TRANSAKSI -> Icons.Default.AccountBalanceWallet
        NotificationCategory.TARGET_DANA -> Icons.Default.TrackChanges
        NotificationCategory.RAPBM -> Icons.Default.Calculate
        NotificationCategory.AUDIT -> Icons.Default.History
        NotificationCategory.BACKUP -> Icons.Default.Backup
        NotificationCategory.SECURITY -> Icons.Default.Security
        NotificationCategory.SYSTEM -> Icons.Default.Info
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val diffMs = System.currentTimeMillis() - timestamp
    val diffHours = diffMs / 3600_000L
    return when {
        diffHours < 1 -> "Baru saja"
        diffHours < 24 -> "$diffHours jam lalu"
        diffHours < 48 -> "Kemarin, ${SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date(timestamp))}"
        else -> SimpleDateFormat("d MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(timestamp))
    }
}
