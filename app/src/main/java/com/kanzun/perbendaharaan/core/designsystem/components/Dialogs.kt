package com.kanzun.perbendaharaan.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kanzun.perbendaharaan.core.designsystem.KanzunShapes
import com.kanzun.perbendaharaan.core.designsystem.neomorphic
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "Ya, Lanjutkan",
    dismissText: String = "Batal",
    isDestructive: Boolean = false,
) {
    AppAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            if (isDestructive) {
                SecondaryButton(
                    text = confirmText,
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                )
            } else {
                PrimaryButton(
                    text = confirmText,
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                )
            }
        },
        dismissButton = {
            AppOutlinedButton(
                text = dismissText,
                onClick = onDismiss,
            )
        },
        shape = com.kanzun.perbendaharaan.core.designsystem.KanzunShapes.Card,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

/** Common viewport and IME boundary for every form/detail popup. */
@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = properties.dismissOnBackPress,
            dismissOnClickOutside = properties.dismissOnClickOutside,
            securePolicy = properties.securePolicy,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding(),
            contentAlignment = androidx.compose.ui.Alignment.Center,
        ) {
            // Darkened scrim backdrop (70% opacity black)
            Box(
                Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.70f))
                    .pointerInput(onDismissRequest, properties.dismissOnClickOutside) {
                        detectTapGestures { if (properties.dismissOnClickOutside) onDismissRequest() }
                    }
            )

            // Opaque solid popup container card
            Surface(
                shape = KanzunShapes.Card,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .heightIn(max = maxHeight * 0.9f),
            ) {
                content()
            }
        }
    }
}

@Composable
fun AppAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = KanzunShapes.Card,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    titleContentColor: Color = MaterialTheme.colorScheme.onSurface,
    textContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    AppDialog(onDismissRequest = onDismissRequest) {
        Surface(modifier = modifier.fillMaxWidth(), shape = shape, color = containerColor) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (title != null) {
                    androidx.compose.runtime.CompositionLocalProvider(
                        androidx.compose.material3.LocalContentColor provides titleContentColor,
                    ) { androidx.compose.material3.ProvideTextStyle(MaterialTheme.typography.headlineSmall, title) }
                }
                if (text != null) {
                    Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
                        androidx.compose.runtime.CompositionLocalProvider(
                            androidx.compose.material3.LocalContentColor provides textContentColor,
                        ) { text() }
                    }
                }
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    confirmButton()
                    dismissButton?.invoke()
                }
            }
        }
    }
}
