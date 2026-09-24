package com.kanzun.perbendaharaan.feature.fundraising.presentation

import androidx.compose.foundation.layout.heightIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import com.kanzun.perbendaharaan.core.designsystem.components.AppAlertDialog as AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.kanzun.perbendaharaan.core.designsystem.components.AppTextField as OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanzun.perbendaharaan.core.designsystem.components.AppDialog as Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanzun.perbendaharaan.core.database.entity.DonationEntity
import com.kanzun.perbendaharaan.core.database.entity.FundraisingTargetEntity
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.Spacing
import com.kanzun.perbendaharaan.core.designsystem.components.AppCard
import com.kanzun.perbendaharaan.core.designsystem.components.ChipStatusType
import com.kanzun.perbendaharaan.core.designsystem.components.EmptyState
import com.kanzun.perbendaharaan.core.designsystem.components.ErrorState
import com.kanzun.perbendaharaan.core.designsystem.components.LoadingState
import com.kanzun.perbendaharaan.core.designsystem.components.PrimaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.ResponsiveContentContainer
import com.kanzun.perbendaharaan.core.designsystem.components.SecondaryButton
import com.kanzun.perbendaharaan.core.designsystem.components.StatusChip
import com.kanzun.perbendaharaan.core.model.Money
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PrimaryBlue = Color(0xFF0D47A1)
private val SecondaryGreen = Color(0xFF2E7D32)
private val AccentAmber = Color(0xFFF9B637)
private val TrackBgColor = Color(0xFFE0E0E0)
private val DarkTrackBgColor = Color(0xFF37474F)

@Composable
fun FundraisingScreen(
    modifier: Modifier = Modifier,
    viewModel: FundraisingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var isFabExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            val scrollState = rememberScrollState()

            ResponsiveContentContainer {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    when {
                        uiState.isLoading -> {
                            LoadingState(count = 3)
                        }

                        uiState.errorMessage != null -> {
                            ErrorState(
                                title = "Terjadi Kesalahan",
                                message = uiState.errorMessage!!,
                                onRetry = { viewModel.loadFundraisingData() },
                            )
                        }

                        else -> {
                            // 1. RINGKASAN TARGET & DONASI + PIE CHART CARD
                            SummaryAndPieChartCard(uiState = uiState)

                            // 2. SECTION TITLE: DAFTAR TARGET DANA
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Daftar Target Dana",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "${uiState.allTargets.size} Target",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            // 3. TARGET CARDS LIST
                            if (uiState.allTargets.isEmpty()) {
                                EmptyState(
                                    title = "Belum Ada Target Dana",
                                    description = "Belum ada program penggalangan dana yang dibuat.",
                                    actionText = "Buat Target Baru",
                                    onActionClick = { viewModel.openTargetForm() },
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    uiState.allTargets.forEach { target ->
                                        val metrics = uiState.targetMetrics[target.id]
                                        TargetCardItem(
                                            target = target,
                                            metrics = metrics,
                                            onClick = { viewModel.selectTargetForDetail(target) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. FAB & SPEED DIAL OVERLAY
        FabSpeedDialOverlay(
            isExpanded = isFabExpanded,
            onToggle = { isFabExpanded = !isFabExpanded },
            onActionSelected = { action ->
                isFabExpanded = false
                when (action) {
                    FabAction.CREATE_TARGET -> viewModel.openTargetForm()
                    FabAction.RECORD_FUND -> viewModel.openFundForm()
                    FabAction.TRANSFER -> viewModel.openTransferForm()
                }
            },
        )
    }

    // DIALOGS & MODALS
    if (uiState.isTargetFormOpen) {
        CreateTargetDialog(
            onDismiss = { viewModel.closeTargetForm() },
            onSubmit = { title, amount, duration ->
                viewModel.createTarget(title, amount, duration)
            },
        )
    }

    if (uiState.isFundFormOpen) {
        RecordFundDialog(
            targets = uiState.allTargets.filter { it.status != "Dibatalkan" },
            onDismiss = { viewModel.closeFundForm() },
            onSubmit = { targetId, isKasAllocation, donorOrNote, amount ->
                viewModel.recordFund(targetId, isKasAllocation, donorOrNote, amount)
            },
        )
    }

    if (uiState.isTransferFormOpen) {
        TransferFundsDialog(
            targets = uiState.allTargets.filter { it.status != "Dibatalkan" },
            targetMetrics = uiState.targetMetrics,
            onDismiss = { viewModel.closeTransferForm() },
            onSubmit = { fromId, toId, amount, note ->
                viewModel.transferFunds(fromId, toId, amount, note)
            },
        )
    }

    uiState.selectedTargetForAction?.let { target ->
        if (uiState.isSalurkanFormOpen) {
            val available = uiState.targetMetrics[target.id]?.availableRemainingInCents ?: 0L
            SalurkanDanaDialog(
                target = target,
                availableAmountCents = available,
                onDismiss = { viewModel.closeSalurkanForm() },
                onSubmit = { amount, purpose ->
                    viewModel.salurkanDana(target.id, amount, purpose)
                },
            )
        }

        if (uiState.isEditFormOpen) {
            EditTargetDialog(
                target = target,
                onDismiss = { viewModel.closeEditForm() },
                onSubmit = { title, amount, duration ->
                    viewModel.editTarget(target.id, title, amount, duration)
                },
            )
        }

        if (uiState.isCancelFormOpen) {
            CancelTargetDialog(
                target = target,
                onDismiss = { viewModel.closeCancelForm() },
                onSubmit = { reason ->
                    viewModel.cancelTarget(target.id, reason)
                },
            )
        }
    }

    // TARGET DETAIL VIEW MODAL
    uiState.selectedTargetForDetail?.let { detailTarget ->
        val metrics = uiState.targetMetrics[detailTarget.id]
        TargetDetailDialog(
            target = detailTarget,
            metrics = metrics,
            onDismiss = { viewModel.closeDetail() },
            onSalurkan = {
                viewModel.openSalurkanForm(detailTarget)
            },
            onEdit = {
                viewModel.openEditForm(detailTarget)
            },
            onCancel = {
                viewModel.openCancelForm(detailTarget)
            },
        )
    }
}

// -----------------------------------------------------------------------------
// SUMMARY CARD & PIE CHART
// -----------------------------------------------------------------------------

@Composable
private fun SummaryAndPieChartCard(uiState: FundraisingUiState) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = PrimaryBlue.copy(alpha = 0.3f),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Ringkasan Target Dana",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                StatusChip(
                    text = "${(uiState.overallProgress * 100).toInt()}% Terkumpul",
                    type = ChipStatusType.INFO,
                )
            }

            // FINANCIAL SUMMARY GRID
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryMetricRow("Total Target", uiState.totalTargetAmount.formatRupiah(), isHighlight = true)
                SummaryMetricRow("Terkumpul", uiState.totalCollectedAmount.formatRupiah(), color = PrimaryBlue)
                SummaryMetricRow("Tersalurkan", uiState.totalDistributedAmount.formatRupiah(), color = AccentAmber)
                SummaryMetricRow("Sisa Terkunci Target", uiState.totalAvailableAmount.formatRupiah(), color = SecondaryGreen)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // PIE CHART SOURCE BREAKDOWN
            Text(
                text = "Komposisi Sumber Dana",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            val donasiCents = uiState.totalDonationAmount.amountInCents
            val kasCents = uiState.totalKasAllocationAmount.amountInCents
            val totalSourceCents = donasiCents + kasCents

            val configuration = LocalConfiguration.current
            val isNarrowScreen = configuration.screenWidthDp < 360

            if (isNarrowScreen) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DonutChartCanvas(
                        donasiCents = donasiCents,
                        kasCents = kasCents,
                        modifier = Modifier.size(110.dp),
                    )
                    PieChartLegend(
                        donasiCents = donasiCents,
                        kasCents = kasCents,
                        totalCents = totalSourceCents,
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    DonutChartCanvas(
                        donasiCents = donasiCents,
                        kasCents = kasCents,
                        modifier = Modifier.size(100.dp),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        PieChartLegend(
                            donasiCents = donasiCents,
                            kasCents = kasCents,
                            totalCents = totalSourceCents,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryMetricRow(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    isHighlight: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = if (isHighlight) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.3).sp,
            color = color,
        )
    }
}

@Composable
private fun DonutChartCanvas(
    donasiCents: Long,
    kasCents: Long,
    modifier: Modifier = Modifier,
) {
    val total = donasiCents + kasCents
    val donasiSweep = if (total > 0) (donasiCents.toFloat() / total.toFloat()) * 360f else 180f
    val kasSweep = if (total > 0) (kasCents.toFloat() / total.toFloat()) * 360f else 180f

    Canvas(modifier = modifier) {
        val strokeWidth = 24.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)
        val topLeft = Offset(center.x - radius, center.y - radius)
        val arcSize = Size(radius * 2, radius * 2)

        if (total == 0L) {
            drawArc(
                color = TrackBgColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth),
            )
        } else {
            // Donasi slice (Primary Blue)
            drawArc(
                color = PrimaryBlue,
                startAngle = -90f,
                sweepAngle = donasiSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth),
            )
            // Alokasi Kas slice (Accent Amber)
            drawArc(
                color = AccentAmber,
                startAngle = -90f + donasiSweep,
                sweepAngle = kasSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth),
            )
        }
    }
}

@Composable
private fun PieChartLegend(
    donasiCents: Long,
    kasCents: Long,
    totalCents: Long,
) {
    val donasiPct = if (totalCents > 0) ((donasiCents.toFloat() / totalCents.toFloat()) * 100).toInt() else 0
    val kasPct = if (totalCents > 0) ((kasCents.toFloat() / totalCents.toFloat()) * 100).toInt() else 0

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LegendItem(
            color = PrimaryBlue,
            label = "Donasi",
            amountText = Money.of(donasiCents).formatRupiah(),
            percentageText = "$donasiPct%",
        )
        LegendItem(
            color = AccentAmber,
            label = "Alokasi Kas",
            amountText = Money.of(kasCents).formatRupiah(),
            percentageText = "$kasPct%",
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    amountText: String,
    percentageText: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = amountText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "($percentageText)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// -----------------------------------------------------------------------------
// TARGET CARD ITEM WITH DUAL PROGRESS INDICATOR
// -----------------------------------------------------------------------------

@Composable
private fun TargetCardItem(
    target: FundraisingTargetEntity,
    metrics: TargetDetailMetrics?,
    onClick: () -> Unit,
) {
    val isCancelled = target.status == "Dibatalkan"
    val isCompleted = target.status == "Selesai" || target.status == "Tercapai"

    val statusType = when {
        isCancelled -> ChipStatusType.ERROR
        isCompleted -> ChipStatusType.SUCCESS
        else -> ChipStatusType.INFO
    }

    val collectedCents = metrics?.collectedAmountInCents ?: target.collectedAmountInCents
    val distributedCents = metrics?.distributedAmountInCents ?: 0L
    val remainingCents = metrics?.availableRemainingInCents ?: (collectedCents - distributedCents).coerceAtLeast(0L)

    val collectedProgress = metrics?.collectedProgress ?: if (target.targetAmountInCents > 0) {
        (collectedCents.toFloat() / target.targetAmountInCents.toFloat()).coerceAtMost(1f)
    } else 0f

    val distributedProgress = metrics?.distributedProgress ?: if (target.targetAmountInCents > 0) {
        (distributedCents.toFloat() / target.targetAmountInCents.toFloat()).coerceAtMost(1f)
    } else 0f

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // CARD HEADER: TITLE + STATUS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = target.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusChip(
                    text = target.status,
                    type = statusType,
                )
            }

            // NOMINAL SUMMARY: Terkumpul / Target
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = Money.of(collectedCents).formatRupiah(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    color = PrimaryBlue,
                )
                Text(
                    text = "/ ${Money.of(target.targetAmountInCents).formatRupiah()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.2).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // DUAL PROGRESS TRACK
            DualProgressBar(
                collectedProgress = collectedProgress,
                distributedProgress = distributedProgress,
            )

            // PROGRESS PERCENTAGE READOUT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${(collectedProgress * 100).toInt()}% Terkumpul",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                )
                Text(
                    text = "${(distributedProgress * 100).toInt()}% Tersalurkan",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AccentAmber,
                )
            }

            // THREE METRIC FOOTER
            Surface(
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MetricMiniCell("Terkumpul", Money.of(collectedCents).formatRupiah())
                    MetricMiniCell("Tersalurkan", Money.of(distributedCents).formatRupiah())
                    MetricMiniCell("Sisa", Money.of(remainingCents).formatRupiah())
                }
            }
        }
    }
}

@Composable
private fun DualProgressBar(
    collectedProgress: Float,
    distributedProgress: Float,
    modifier: Modifier = Modifier,
) {
    val isDark = MaterialTheme.colorScheme.surface.red < 0.2f
    val trackBg = if (isDark) DarkTrackBgColor else TrackBgColor

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(trackBg),
    ) {
        // Track 1: Terkumpul / Masuk (Primary Blue)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(collectedProgress)
                .clip(RoundedCornerShape(3.dp))
                .background(PrimaryBlue),
        )
        // Track 2: Tersalurkan (Accent Amber overlay)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(distributedProgress.coerceAtMost(collectedProgress))
                .clip(RoundedCornerShape(3.dp))
                .background(AccentAmber),
        )
    }
}

@Composable
private fun MetricMiniCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.2).sp,
        )
    }
}

// -----------------------------------------------------------------------------
// FAB & SPEED DIAL OVERLAY
// -----------------------------------------------------------------------------

private enum class FabAction {
    CREATE_TARGET,
    RECORD_FUND,
    TRANSFER,
}

@Composable
private fun FabSpeedDialOverlay(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onActionSelected: (FabAction) -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "fabRotate",
    )

    // SCRIM BACKDROP
    AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(200)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggle,
                ),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, end = 16.dp),
        contentAlignment = Alignment.BottomEnd,
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // EXPANDED SPEED DIAL ACTIONS STACK
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(180)) + scaleIn(tween(180)),
                exit = fadeOut(tween(180)) + scaleOut(tween(180)),
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedDialItem(
                        icon = Icons.Default.Add,
                        label = "Buat Target Baru",
                        onClick = { onActionSelected(FabAction.CREATE_TARGET) },
                    )
                    SpeedDialItem(
                        icon = Icons.Default.Payments,
                        label = "Catat Donasi & Alokasi",
                        onClick = { onActionSelected(FabAction.RECORD_FUND) },
                    )
                    SpeedDialItem(
                        icon = Icons.Default.SwapHoriz,
                        label = "Transfer Antar Target",
                        onClick = { onActionSelected(FabAction.TRANSFER) },
                    )
                }
            }

            // MAIN FAB BUTTON (56x56dp, Primary Blue #0D47A1)
            FloatingActionButton(
                onClick = onToggle,
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (isExpanded) "Tutup Menu" else "Tambah Target Dana",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation),
                )
            }
        }
    }
}

@Composable
private fun SpeedDialItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 1.dp,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = PrimaryBlue,
            contentColor = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 1.dp,
            modifier = Modifier.size(40.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// -----------------------------------------------------------------------------
// DIALOG 1: BUAT TARGET BARU
// -----------------------------------------------------------------------------

@Composable
private fun CreateTargetDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Long, Int) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("60") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    StyledModalDialog(
        title = "Buat Target Baru",
        onDismiss = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nama Target Dana *") },
                placeholder = { Text("misal: Renovasi Tempat Wudhu") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                label = { Text("Target Nominal (Rp) *") },
                placeholder = { Text("0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            OutlinedTextField(
                value = durationText,
                onValueChange = { durationText = it.filter { c -> c.isDigit() } },
                label = { Text("Durasi Penggalangan (Hari)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            Spacer(modifier = Modifier.height(4.dp))

            PrimaryButton(
                text = "Simpan Target",
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                onClick = {
                    val amountCents = amountText.toLongOrNull() ?: 0L
                    val days = durationText.toIntOrNull() ?: 60
                    if (title.isBlank()) {
                        errorMessage = "Nama target harus diisi"
                        return@PrimaryButton
                    }
                    if (amountCents <= 0) {
                        errorMessage = "Target nominal harus lebih dari 0"
                        return@PrimaryButton
                    }
                    onSubmit(title, amountCents, days)
                },
            )
        }
    }
}

// -----------------------------------------------------------------------------
// DIALOG 2: CATAT DONASI DAN ALOKASI
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordFundDialog(
    targets: List<FundraisingTargetEntity>,
    onDismiss: () -> Unit,
    onSubmit: (String, Boolean, String, Long) -> Unit,
) {
    var selectedTargetId by remember { mutableStateOf(targets.firstOrNull()?.id ?: "") }
    var isKasAllocation by remember { mutableStateOf(false) } // false = Donasi, true = Alokasi Kas
    var donorOrNote by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    StyledModalDialog(
        title = "Catat Donasi & Alokasi",
        onDismiss = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // SEGMENTED SOURCE SELECTOR
            Text(
                text = "Sumber Dana *",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (!isKasAllocation) PrimaryBlue else Color.Transparent)
                        .clickable { isKasAllocation = false }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Donasi",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (!isKasAllocation) FontWeight.Normal else FontWeight.Light,
                        color = if (!isKasAllocation) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isKasAllocation) PrimaryBlue else Color.Transparent)
                        .clickable { isKasAllocation = true }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Alokasi Kas Masjid",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isKasAllocation) FontWeight.Normal else FontWeight.Light,
                        color = if (isKasAllocation) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // TARGET DROPDOWN
            var expanded by remember { mutableStateOf(false) }
            val selectedTitle = targets.find { it.id == selectedTargetId }?.title ?: "Pilih Target Dana"

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    value = selectedTitle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Target Dana *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).menuAnchor(),
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    targets.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t.title) },
                            onClick = {
                                selectedTargetId = t.id
                                expanded = false
                            },
                        )
                    }
                }
            }

            OutlinedTextField(
                value = donorOrNote,
                onValueChange = { donorOrNote = it },
                label = { Text(if (isKasAllocation) "Catatan Alokasi (Opsional)" else "Nama Donatur (Opsional)") },
                placeholder = { Text(if (isKasAllocation) "misal: Kas Umum Ramadan" else "misal: Hamba Allah / Bpk. Ahmad") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                label = { Text("Nominal Dana (Rp) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            Spacer(modifier = Modifier.height(4.dp))

            PrimaryButton(
                text = "Simpan Setoran Dana",
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                onClick = {
                    val amountCents = amountText.toLongOrNull() ?: 0L
                    if (selectedTargetId.isBlank()) {
                        errorMessage = "Pilih target dana terlebih dahulu"
                        return@PrimaryButton
                    }
                    if (amountCents <= 0) {
                        errorMessage = "Nominal dana harus lebih dari 0"
                        return@PrimaryButton
                    }
                    onSubmit(selectedTargetId, isKasAllocation, donorOrNote, amountCents)
                },
            )
        }
    }
}

// -----------------------------------------------------------------------------
// DIALOG 3: TRANSFER ANTAR TARGET
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransferFundsDialog(
    targets: List<FundraisingTargetEntity>,
    targetMetrics: Map<String, TargetDetailMetrics>,
    onDismiss: () -> Unit,
    onSubmit: (String, String, Long, String) -> Unit,
) {
    var fromTargetId by remember { mutableStateOf(targets.firstOrNull()?.id ?: "") }
    var toTargetId by remember { mutableStateOf(targets.getOrNull(1)?.id ?: "") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fromMetrics = targetMetrics[fromTargetId]
    val maxAvailable = fromMetrics?.availableRemainingInCents ?: 0L

    StyledModalDialog(
        title = "Transfer Antar Target",
        onDismiss = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // FROM TARGET
            var expandedFrom by remember { mutableStateOf(false) }
            val fromTitle = targets.find { it.id == fromTargetId }?.title ?: "Pilih Target Asal"

            ExposedDropdownMenuBox(
                expanded = expandedFrom,
                onExpandedChange = { expandedFrom = !expandedFrom },
            ) {
                OutlinedTextField(
                    value = fromTitle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dari Target (Asal) *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrom) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).menuAnchor(),
                )
                ExposedDropdownMenu(expanded = expandedFrom, onDismissRequest = { expandedFrom = false }) {
                    targets.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t.title) },
                            onClick = {
                                fromTargetId = t.id
                                expandedFrom = false
                            },
                        )
                    }
                }
            }

            Text(
                text = "Sisa dana tersedia di target asal: ${Money.of(maxAvailable).formatRupiah()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // TO TARGET
            var expandedTo by remember { mutableStateOf(false) }
            val toTitle = targets.find { it.id == toTargetId }?.title ?: "Pilih Target Tujuan"

            ExposedDropdownMenuBox(
                expanded = expandedTo,
                onExpandedChange = { expandedTo = !expandedTo },
            ) {
                OutlinedTextField(
                    value = toTitle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ke Target (Tujuan) *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).menuAnchor(),
                )
                ExposedDropdownMenu(expanded = expandedTo, onDismissRequest = { expandedTo = false }) {
                    targets.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t.title) },
                            onClick = {
                                toTargetId = t.id
                                expandedTo = false
                            },
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                label = { Text("Nominal Transfer (Rp) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan Transfer (Opsional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            Spacer(modifier = Modifier.height(4.dp))

            PrimaryButton(
                text = "Transfer Dana",
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                onClick = {
                    val amountCents = amountText.toLongOrNull() ?: 0L
                    if (fromTargetId == toTargetId) {
                        errorMessage = "Target asal dan tujuan tidak boleh sama"
                        return@PrimaryButton
                    }
                    if (amountCents <= 0) {
                        errorMessage = "Nominal transfer harus lebih dari 0"
                        return@PrimaryButton
                    }
                    if (amountCents > maxAvailable) {
                        errorMessage = "Nominal melebihi sisa dana tersedia (${Money.of(maxAvailable).formatRupiah()})"
                        return@PrimaryButton
                    }
                    onSubmit(fromTargetId, toTargetId, amountCents, note)
                },
            )
        }
    }
}

// -----------------------------------------------------------------------------
// DIALOG 4: SALURKAN DANA
// -----------------------------------------------------------------------------

@Composable
private fun SalurkanDanaDialog(
    target: FundraisingTargetEntity,
    availableAmountCents: Long,
    onDismiss: () -> Unit,
    onSubmit: (Long, String) -> Unit,
) {
    var amountText by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    StyledModalDialog(
        title = "Salurkan Dana Target",
        onDismiss = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Program: ${target.title}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Sisa Dana Tersedia: ${Money.of(availableAmountCents).formatRupiah()}",
                style = MaterialTheme.typography.labelMedium,
                color = SecondaryGreen,
            )

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                label = { Text("Nominal Penyaluran (Rp) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            OutlinedTextField(
                value = purpose,
                onValueChange = { purpose = it },
                label = { Text("Tujuan / Keperluan Penyaluran *") },
                placeholder = { Text("misal: Pembelian material renovasi keramik") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            Spacer(modifier = Modifier.height(4.dp))

            PrimaryButton(
                text = "Salurkan Dana",
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                onClick = {
                    val amountCents = amountText.toLongOrNull() ?: 0L
                    if (amountCents <= 0) {
                        errorMessage = "Nominal penyaluran harus lebih dari 0"
                        return@PrimaryButton
                    }
                    if (amountCents > availableAmountCents) {
                        errorMessage = "Nominal melebihi dana tersedia (${Money.of(availableAmountCents).formatRupiah()})"
                        return@PrimaryButton
                    }
                    if (purpose.isBlank()) {
                        errorMessage = "Tujuan penyaluran wajib diisi"
                        return@PrimaryButton
                    }
                    onSubmit(amountCents, purpose)
                },
            )
        }
    }
}

// -----------------------------------------------------------------------------
// DIALOG 5: EDIT TARGET
// -----------------------------------------------------------------------------

@Composable
private fun EditTargetDialog(
    target: FundraisingTargetEntity,
    onDismiss: () -> Unit,
    onSubmit: (String, Long, Int) -> Unit,
) {
    var title by remember { mutableStateOf(target.title) }
    var amountText by remember { mutableStateOf(target.targetAmountInCents.toString()) }
    var durationText by remember { mutableStateOf("60") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    StyledModalDialog(
        title = "Edit Target Dana",
        onDismiss = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nama Target Dana *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                label = { Text("Target Nominal (Rp) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            OutlinedTextField(
                value = durationText,
                onValueChange = { durationText = it.filter { c -> c.isDigit() } },
                label = { Text("Perpanjangan Durasi (Hari)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            )

            Spacer(modifier = Modifier.height(4.dp))

            PrimaryButton(
                text = "Simpan Perubahan",
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                onClick = {
                    val amountCents = amountText.toLongOrNull() ?: 0L
                    val days = durationText.toIntOrNull() ?: 0
                    if (title.isBlank()) {
                        errorMessage = "Nama target harus diisi"
                        return@PrimaryButton
                    }
                    if (amountCents <= 0) {
                        errorMessage = "Target nominal harus lebih dari 0"
                        return@PrimaryButton
                    }
                    onSubmit(title, amountCents, days)
                },
            )
        }
    }
}

// -----------------------------------------------------------------------------
// DIALOG 6: BATALKAN TARGET
// -----------------------------------------------------------------------------

@Composable
private fun CancelTargetDialog(
    target: FundraisingTargetEntity,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var reason by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    StyledModalDialog(
        title = "Batalkan Target Dana",
        onDismiss = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Target akan dibatalkan. Sisa dana donasi yang belum tersalurkan akan dikembalikan ke Kas Umum Masjid.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Alasan Pembatalan *") },
                placeholder = { Text("misal: Program dialihkan atau telah selesai secara eksternal") },
                singleLine = false,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SecondaryButton(
                    text = "Batal",
                    modifier = Modifier.weight(1f).heightIn(min = 50.dp),
                    onClick = onDismiss,
                )
                PrimaryButton(
                    text = "Batalkan Target",
                    modifier = Modifier.weight(1f).heightIn(min = 50.dp),
                    onClick = {
                        if (reason.isBlank()) {
                            errorMessage = "Alasan pembatalan wajib diisi"
                            return@PrimaryButton
                        }
                        onSubmit(reason)
                    },
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TARGET DETAIL MODAL VIEW
// -----------------------------------------------------------------------------

@Composable
private fun TargetDetailDialog(
    target: FundraisingTargetEntity,
    metrics: TargetDetailMetrics?,
    onDismiss: () -> Unit,
    onSalurkan: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val isCancelled = target.status == "Dibatalkan"

    StyledModalDialog(
        title = target.title,
        onDismiss = onDismiss,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // STATUS BADGE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Detail Program Target",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                StatusChip(
                    text = target.status,
                    type = when (target.status) {
                        "Dibatalkan" -> ChipStatusType.ERROR
                        "Selesai", "Tercapai" -> ChipStatusType.SUCCESS
                        else -> ChipStatusType.INFO
                    },
                )
            }

            // FINANCIAL SUMMARY BOX
            val collectedCents = metrics?.collectedAmountInCents ?: target.collectedAmountInCents
            val distributedCents = metrics?.distributedAmountInCents ?: 0L
            val availableCents = metrics?.availableRemainingInCents ?: (collectedCents - distributedCents).coerceAtLeast(0L)

            Surface(
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SummaryMetricRow("Target Nominal", Money.of(target.targetAmountInCents).formatRupiah(), isHighlight = true)
                    SummaryMetricRow("Dana Terkumpul", Money.of(collectedCents).formatRupiah(), color = PrimaryBlue)
                    SummaryMetricRow("Dana Tersalurkan", Money.of(distributedCents).formatRupiah(), color = AccentAmber)
                    SummaryMetricRow("Sisa Tersedia", Money.of(availableCents).formatRupiah(), color = SecondaryGreen)
                }
            }

            // FUND SOURCES BREAKDOWN
            val donasiCents = metrics?.donationAmountInCents ?: 0L
            val kasCents = metrics?.kasAllocationAmountInCents ?: 0L

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    modifier = Modifier.weight(1f),
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Donasi", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue)
                        Text(
                            Money.of(donasiCents).formatRupiah(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.2).sp,
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    color = AccentAmber.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f),
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Alokasi Kas", style = MaterialTheme.typography.labelSmall, color = AccentAmber)
                        Text(
                            Money.of(kasCents).formatRupiah(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.2).sp,
                        )
                    }
                }
            }

            // HISTORY TABS
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Dana Masuk (${metrics?.incomingHistory?.size ?: 0})") },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Penyaluran (${metrics?.distributionHistory?.size ?: 0})") },
                )
            }

            if (selectedTab == 0) {
                val incomingList = metrics?.incomingHistory ?: emptyList()
                if (incomingList.isEmpty()) {
                    Text(
                        text = "Belum ada riwayat dana masuk.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        incomingList.forEach { don ->
                            HistoryRow(
                                title = don.donorName,
                                dateText = formatDate(don.timestamp),
                                amountText = "+${Money.of(don.amountInCents).formatRupiah()}",
                                isIncoming = true,
                            )
                        }
                    }
                }
            } else {
                val distList = metrics?.distributionHistory ?: emptyList()
                if (distList.isEmpty()) {
                    Text(
                        text = "Belum ada riwayat penyaluran.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        distList.forEach { dist ->
                            HistoryRow(
                                title = dist.donorName.removePrefix("Penyaluran: "),
                                dateText = formatDate(dist.timestamp),
                                amountText = "-${Money.of(dist.amountInCents).formatRupiah()}",
                                isIncoming = false,
                            )
                        }
                    }
                }
            }

            // BOTTOM ACTION BUTTONS
            if (!isCancelled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PrimaryButton(
                        text = "Salurkan Dana",
                        icon = Icons.Default.Send,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                        onClick = {
                            onDismiss()
                            onSalurkan()
                        },
                    )
                    SecondaryButton(
                        text = "Edit",
                        icon = Icons.Default.Edit,
                        modifier = Modifier.heightIn(min = 48.dp),
                        onClick = {
                            onDismiss()
                            onEdit()
                        },
                    )
                    SecondaryButton(
                        text = "Batalkan",
                        modifier = Modifier.heightIn(min = 48.dp),
                        onClick = {
                            onDismiss()
                            onCancel()
                        },
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Target ini telah dibatalkan. Tidak dapat menerima transaksi baru.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(
    title: String,
    dateText: String,
    amountText: String,
    isIncoming: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = amountText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp,
                color = if (isIncoming) PrimaryBlue else AccentAmber,
            )
        }
    }
}

// -----------------------------------------------------------------------------
// DIALOG WRAPPER WITH PRECISE RESPONSIVE BOUNDS (WIDTH - 32DP, MAX 560DP)
// -----------------------------------------------------------------------------

@Composable
private fun StyledModalDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    com.kanzun.perbendaharaan.core.designsystem.components.AppDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
                content()
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}
