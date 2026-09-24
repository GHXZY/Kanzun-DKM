package com.kanzun.perbendaharaan.core.designsystem.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.TextButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanzun.perbendaharaan.R
import com.kanzun.perbendaharaan.core.designsystem.Elevation
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
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
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.width(Spacing.MD))
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(KanzunShapes.SmallComponent)
                    .clickable { onActionClick() }
                    .padding(horizontal = Spacing.SM, vertical = Spacing.XS),
            )
        }
    }
}

@Composable
fun AppTopBar(
    mosqueName: String = "Masjid Agung Al-Mubarak",
    mosqueAddress: String = "Jl. Ahmad Yani No. 45, Jakarta",
    onNotificationClick: () -> Unit = {},
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    unreadCount: Int = 0,
    hasUnreadNotifications: Boolean = unreadCount > 0,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth().statusBarsPadding(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = Elevation.Level0,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.MD, vertical = Spacing.MDS),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = KanzunShapes.SmallComponent,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kanzun_logo),
                            contentDescription = "Logo Kanzun",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.MDS))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mosqueName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = mosqueAddress,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.SM))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIconButton(
                    icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Ubah Tema",
                    onClick = onToggleTheme,
                )

                Spacer(modifier = Modifier.width(Spacing.SM))

                Box {
                    AppIconButton(
                        icon = Icons.Default.Notifications,
                        contentDescription = "Notifikasi",
                        onClick = onNotificationClick,
                    )

                    if (unreadCount > 0 || hasUnreadNotifications) {
                        val badgeText = when {
                            unreadCount > 99 -> "99+"
                            unreadCount > 0 -> unreadCount.toString()
                            else -> ""
                        }

                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 2.dp, end = 2.dp),
                            shape = KanzunShapes.Pill,
                            color = MaterialTheme.colorScheme.error,
                        ) {
                            Box(
                                modifier = Modifier.padding(
                                    horizontal = if (badgeText.isEmpty()) 4.dp else 5.dp,
                                    vertical = if (badgeText.isEmpty()) 4.dp else 2.dp,
                                ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (badgeText.isNotEmpty()) {
                                    Text(
                                        text = badgeText,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 10.sp,
                                        ),
                                        color = MaterialTheme.colorScheme.onError,
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
fun ContextualFeatureHeader(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth().statusBarsPadding(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = Elevation.Level0,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.MD, vertical = Spacing.MDS),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                AppIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    onClick = onBackClick,
                )

                Spacer(modifier = Modifier.width(Spacing.MDS))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            if (actionIcon != null && onActionClick != null) {
                Spacer(modifier = Modifier.width(Spacing.SM))
                AppIconButton(
                    icon = actionIcon,
                    contentDescription = "Aksi",
                    onClick = onActionClick,
                )
            }
        }
    }
}

@Composable
fun IsolatedFeatureTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth().statusBarsPadding(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = Elevation.Level0,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.MD, vertical = Spacing.MDS),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AppIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                onClick = onBackClick,
            )

            Text(
                text = title,
                modifier = Modifier.weight(1f).padding(start = Spacing.MDS),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

