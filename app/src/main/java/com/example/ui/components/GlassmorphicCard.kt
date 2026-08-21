package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    glowColor: Color? = null,
    cornerRadius: Dp = 26.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.985f else 1.0f,
        animationSpec = spring(stiffness = 500f),
        label = "cardScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (glowColor != null) 14.dp else 8.dp,
                shape = shape,
                spotColor = glowColor ?: Color(0x66000000),
                ambientColor = glowColor?.copy(alpha = 0.3f) ?: Color(0x22000000)
            )
    ) {
        // High-Performance Glass Container with subtle background gradient & border
        Box(
            modifier = Modifier
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x2BFFFFFF),
                            Color(0x10FFFFFF),
                            Color(0x1F000000)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x50FFFFFF),
                            glowColor?.copy(alpha = 0.4f) ?: Color(0x1AFFFFFF),
                            Color(0x08FFFFFF)
                        )
                    ),
                    shape = shape
                )
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        )
                    } else Modifier
                )
        ) {
            content()
        }
    }
}
