package com.kanzun.perbendaharaan.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

/**
 * Stripe Precision Depth: Crisp 1px hairline border + tight micro-shadow with cool navy undertone.
 */
@Composable
fun Modifier.neomorphic(shape: Shape = KanzunShapes.Card, inset: Boolean = false): Modifier {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.2f
    val hairlineColor = if (dark) Color(0x28FFFFFF) else LightBorder
    val shadowColor = if (dark) Color(0x33000000) else Color(0x0D061B31)

    return drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        val strokeWidth = 1.dp.toPx()

        onDrawWithContent {
            if (!inset) {
                // Stripe Level 1 Tight Micro-Elevation
                val shadowOffset = 1.5.dp.toPx()
                translate(0f, shadowOffset) {
                    drawOutline(outline, shadowColor, style = Stroke(strokeWidth * 1.5f))
                }
            }
            drawContent()
            // Crisp hairline border (Stripe signature precision)
            drawOutline(outline, hairlineColor, style = Stroke(strokeWidth))
        }
    }
}
