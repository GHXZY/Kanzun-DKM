package com.kanzun.perbendaharaan.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.ui.semantics.Role
import com.kanzun.perbendaharaan.core.designsystem.neomorphic
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.text.style.TextOverflow
import com.kanzun.perbendaharaan.core.designsystem.Elevation
import com.kanzun.perbendaharaan.core.designsystem.IconSize
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing

data class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
)

val MainNavItems = listOf(
    NavItem("dashboard", "Beranda", Icons.Default.Home),
    NavItem("treasurer", "Bendahara", Icons.Default.AccountBalance),
    NavItem("reports", "Laporan", Icons.Default.Assessment),
    NavItem("settings", "Pengaturan", Icons.Default.Settings),
)

@Composable
fun FloatingNavBar(
    currentRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    items: List<NavItem> = MainNavItems,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .padding(horizontal = Spacing.MD, vertical = Spacing.MDS)
                .heightIn(min = 72.dp)
                .neomorphic(KanzunShapes.FloatingNavbar),
            shape = KanzunShapes.FloatingNavbar,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.SM, vertical = Spacing.SM)
                    .selectableGroup(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    val activeBackgroundColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                        label = "NavBgAnimation",
                    )

                    val activeContentColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                        label = "NavContentAnimation",
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                            .clip(KanzunShapes.Pill)
                            .background(activeBackgroundColor)
                            .selectable(selected = isSelected, role = Role.Tab, onClick = { onItemSelected(item.route) })
                            .padding(horizontal = Spacing.XS, vertical = Spacing.SM),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = activeContentColor,
                                modifier = Modifier.size(IconSize.Standard),
                            )
                            if (isSelected) {
                                
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = activeContentColor,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
