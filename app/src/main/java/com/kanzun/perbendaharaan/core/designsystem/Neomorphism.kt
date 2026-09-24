package com.kanzun.perbendaharaan.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

/** Cached, bounded soft depth. Does not change layout, input or accessibility. */
@Composable
fun Modifier.neomorphic(shape: Shape = KanzunShapes.Card, inset: Boolean = false): Modifier {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.2f
    val highlight = if (dark) Color(0xFF52677F) else Color.White
    val shadow = if (dark) Color.Black else Color(0xFF879AB0)
    return drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        val edge = Brush.linearGradient(
            listOf(if (inset) shadow.copy(alpha = 0.22f) else highlight.copy(alpha = 0.65f),
                Color.Transparent,
                if (inset) highlight.copy(alpha = 0.55f) else shadow.copy(alpha = 0.14f)),
            Offset.Zero, Offset(size.width, size.height),
        )
        onDrawWithContent {
            if (!inset) {
                // Layered translucent outlines produce diffuse depth on API 26+ without blur layers.
                for (layer in 6 downTo 1) {
                    val offset = (layer * 0.6f).dp.toPx()
                    val stroke = Stroke((layer * 1.2f).dp.toPx())
                    translate(offset, offset) { drawOutline(outline, shadow.copy(alpha = if (dark) 0.045f else 0.025f), style = stroke) }
                    translate(-offset, -offset) { drawOutline(outline, highlight.copy(alpha = if (dark) 0.025f else 0.09f), style = stroke) }
                }
            }
            drawContent()
            drawOutline(outline, edge, style = Stroke(1.dp.toPx()))
        }
    }
}
