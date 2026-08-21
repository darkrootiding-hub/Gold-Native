package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MetalType
import com.example.data.model.PriceHistoryPoint
import com.example.util.DateUtils
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistoryChartCard(
    goldHistory: List<PriceHistoryPoint>,
    silverHistory: List<PriceHistoryPoint>,
    modifier: Modifier = Modifier
) {
    var selectedMetal by remember { mutableStateOf(MetalType.GOLD) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val points = if (selectedMetal == MetalType.GOLD) goldHistory else silverHistory
    val lineColor = if (selectedMetal == MetalType.GOLD) Color(0xFFECC840) else Color(0xFFBED0E0)
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    val rates = points.map { it.rate }.filter { it > 0 }
    val minRate = rates.minOrNull() ?: 0
    val maxRate = rates.maxOrNull() ?: 0
    val latestRate = rates.lastOrNull() ?: 0

    val inspectedPoint = selectedIndex?.let { idx ->
        if (idx in points.indices) points[idx] else null
    }

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("history_chart_card"),
        cornerRadius = 28.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Header & Metal Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "मूल्य इतिहास (Price History)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Touch chart to inspect 14-day trend",
                        fontSize = 11.sp,
                        color = Color(0xBBFFFFFF)
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22000000))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedMetal == MetalType.GOLD) Color(0xFFD4A520) else Color.Transparent)
                            .clickable {
                                selectedMetal = MetalType.GOLD
                                selectedIndex = null
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("chart_gold_toggle")
                    ) {
                        Text(
                            text = "Gold",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedMetal == MetalType.GOLD) Color.Black else Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedMetal == MetalType.SILVER) Color(0xFF8094A8) else Color.Transparent)
                            .clickable {
                                selectedMetal = MetalType.SILVER
                                selectedIndex = null
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("chart_silver_toggle")
                    ) {
                        Text(
                            text = "Silver",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedMetal == MetalType.SILVER) Color.Black else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inspector Active Tooltip Pill
            if (inspectedPoint != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33000000))
                        .border(1.dp, lineColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val formatted = DateUtils.formatTimestamp(inspectedPoint.timestamp)
                        Text(
                            text = "मिति: ${formatted.date} • समय: ${formatted.time}",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Rate: रु ${numberFormat.format(inspectedPoint.rate)}",
                            fontSize = 13.sp,
                            color = lineColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x15000000))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Tip: Drag across graph to see daily details",
                        fontSize = 11.sp,
                        color = Color(0x99FFFFFF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Chart Canvas
            if (rates.size < 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0x15000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading price history...", fontSize = 12.sp, color = Color(0x88FFFFFF))
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0x18000000))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                        .padding(10.dp)
                        .pointerInput(rates) {
                            detectTapGestures(
                                onPress = { offset ->
                                    val stepX = size.width / (rates.size - 1).coerceAtLeast(1)
                                    val idx = ((offset.x + stepX / 2) / stepX).toInt().coerceIn(0, rates.size - 1)
                                    selectedIndex = idx
                                }
                            )
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    val minVal = (minRate * 0.995).toFloat()
                    val maxVal = (maxRate * 1.005).toFloat()
                    val range = if (maxVal != minVal) maxVal - minVal else 1f

                    val stepX = w / (rates.size - 1).coerceAtLeast(1)

                    val linePath = Path()
                    val areaPath = Path()

                    val coordinates = rates.mapIndexed { index, rate ->
                        val x = index * stepX
                        val normalizedY = (rate - minVal) / range
                        val y = h - (normalizedY * h * 0.82f + h * 0.08f)
                        Offset(x, y)
                    }

                    // Build smooth line and area path
                    coordinates.forEachIndexed { index, point ->
                        if (index == 0) {
                            linePath.moveTo(point.x, point.y)
                            areaPath.moveTo(point.x, h)
                            areaPath.lineTo(point.x, point.y)
                        } else {
                            val prev = coordinates[index - 1]
                            val controlX1 = prev.x + (point.x - prev.x) / 2f
                            val controlY1 = prev.y
                            val controlX2 = prev.x + (point.x - prev.x) / 2f
                            val controlY2 = point.y

                            linePath.cubicTo(controlX1, controlY1, controlX2, controlY2, point.x, point.y)
                            areaPath.cubicTo(controlX1, controlY1, controlX2, controlY2, point.x, point.y)
                        }

                        if (index == coordinates.size - 1) {
                            areaPath.lineTo(point.x, h)
                            areaPath.close()
                        }
                    }

                    // Gradient area fill
                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(lineColor.copy(alpha = 0.45f), Color.Transparent)
                        )
                    )

                    // Stroke line
                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(width = 3.5.dp.toPx())
                    )

                    // Draw dots on coordinates
                    coordinates.forEachIndexed { index, point ->
                        val isSelected = index == selectedIndex
                        drawCircle(
                            color = if (isSelected) Color.White else lineColor,
                            center = point,
                            radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx()
                        )
                        if (isSelected) {
                            drawCircle(
                                color = lineColor,
                                center = point,
                                radius = 10.dp.toPx(),
                                style = Stroke(width = 2.dp.toPx())
                            )
                            // Vertical dashed crosshair line
                            drawLine(
                                color = lineColor.copy(alpha = 0.6f),
                                start = Offset(point.x, 0f),
                                end = Offset(point.x, h),
                                strokeWidth = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Min / Latest / Max Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricChip("न्यूनतम (14d Min)", "रु ${numberFormat.format(minRate)}")
                MetricChip("पछिल्लो (Current)", "रु ${numberFormat.format(latestRate)}")
                MetricChip("अधिकतम (14d Max)", "रु ${numberFormat.format(maxRate)}")
            }
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color(0xAAFFFFFF))
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color.White
        )
    }
}
