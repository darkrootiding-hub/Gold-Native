package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DhakaWeaveBar(
    modifier: Modifier = Modifier,
    height: Dp = 6.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val stripeWidth = 12f

        var x = 0f
        var colorIndex = 0
        val colors = listOf(
            Color(0xFFC8102E), // Crimson Red
            Color(0xFFD4A520), // Gold Accent
            Color(0xFF006A4E), // Deep Green
            Color(0xFF1C132B)  // Dark Velvet Interlace
        )

        while (x < w) {
            val stripeColor = colors[colorIndex % colors.size]
            drawRect(
                color = stripeColor,
                topLeft = androidx.compose.ui.geometry.Offset(x, 0f),
                size = androidx.compose.ui.geometry.Size(stripeWidth, h)
            )
            x += stripeWidth
            colorIndex++
        }

        // Glassy sheen overlay
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0x80FFFFFF),
                    Color(0x10FFFFFF),
                    Color(0x40000000)
                )
            )
        )
    }
}
