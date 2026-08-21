package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketMood
import com.example.data.model.MarketMoodState
import java.text.DecimalFormat

@Composable
fun MarketMoodCard(
    mood: MarketMood,
    modifier: Modifier = Modifier
) {
    val decimalFormat = DecimalFormat("+0.00;-0.00")

    val state = mood.state

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("market_mood_card"),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "बजार अवस्था (Market Mood)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Derived from 7-day gold price momentum",
                        fontSize = 11.sp,
                        color = Color(0xAAFFFFFF)
                    )
                }

                // Mood Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = state.emoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${state.labelNp} (${state.labelEn})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Gradient Meter Bar (Bearish -> Neutral -> Bullish)
            MeterBar(state = state)

            Spacer(modifier = Modifier.height(18.dp))

            // Sub-metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MoodMetric(
                    label = "७ दिनको प्रवृत्ति (7d Trend)",
                    value = "${decimalFormat.format(mood.trendPercent)}%",
                    color = if (mood.trendPercent >= 0) Color(0xFF34D399) else Color(0xFFF87171)
                )

                MoodMetric(
                    label = "निरन्तरता (Streak)",
                    value = if (mood.streakDays > 0) "▲ ${mood.streakDays} days up" else if (mood.streakDays < 0) "▼ ${kotlin.math.abs(mood.streakDays)} days down" else "— Flat",
                    color = if (mood.streakDays > 0) Color(0xFF34D399) else if (mood.streakDays < 0) Color(0xFFF87171) else Color.White
                )

                MoodMetric(
                    label = "अस्थिरता (Volatility)",
                    value = "±${DecimalFormat("#,##0").format(mood.volatility.toInt())} NPR",
                    color = Color(0xFFECC840)
                )
            }
        }
    }
}

@Composable
private fun MeterBar(state: MarketMoodState) {
    // Position 0.0 (Bearish) to 1.0 (Bullish)
    val indicatorRatio = when (state) {
        MarketMoodState.BEARISH -> 0.10f
        MarketMoodState.MILDLY_BEARISH -> 0.30f
        MarketMoodState.NEUTRAL -> 0.50f
        MarketMoodState.MILDLY_BULLISH -> 0.70f
        MarketMoodState.BULLISH -> 0.90f
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFEF4444), // Bearish Red
                        Color(0xFFF59E0B), // Neutral Amber
                        Color(0xFF10B981)  // Bullish Green
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val indicatorX = size.width * indicatorRatio
            val indicatorY = size.height / 2f

            drawCircle(
                color = Color.White,
                center = Offset(indicatorX, indicatorY),
                radius = 10.dp.toPx()
            )
            drawCircle(
                color = Color.Black,
                center = Offset(indicatorX, indicatorY),
                radius = 6.dp.toPx()
            )
        }
    }
}

@Composable
private fun MoodMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color(0xAAFFFFFF))
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = color
        )
    }
}
