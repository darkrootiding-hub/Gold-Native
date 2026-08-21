package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MetalInfo
import com.example.data.model.MetalType
import com.example.data.model.PriceUnit
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SilverPrimary
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.util.DateUtils
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HeroPriceCard(
    metalType: MetalType,
    metalInfo: MetalInfo,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    var selectedUnit by remember { mutableStateOf(PriceUnit.TOLA) }

    val isGold = metalType == MetalType.GOLD
    val accentColor = if (isGold) GoldPrimary else SilverPrimary
    val cardTag = if (isGold) "gold_hero_card" else "silver_hero_card"

    val activeRateValue = when (selectedUnit) {
        PriceUnit.TOLA -> metalInfo.rate
        PriceUnit.GRAM -> metalInfo.perGram.toInt()
        PriceUnit.LAL -> (metalInfo.perGram * PriceUnit.LAL.gramsPerUnit).toInt()
        PriceUnit.TEN_GRAM -> metalInfo.per10g.toInt()
        PriceUnit.OUNCE -> metalInfo.perOunce.toInt()
        PriceUnit.KILOGRAM -> (metalInfo.perGram * 1000).toInt()
    }

    val animatedRate by animateIntAsState(
        targetValue = activeRateValue,
        animationSpec = tween(durationMillis = 600),
        label = "priceAnimation"
    )

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag(cardTag),
        glowColor = accentColor,
        cornerRadius = 32.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = if (isGold) listOf(GoldSecondary, GoldPrimary)
                                    else listOf(Color(0xFFE2E8F0), SilverPrimary)
                                )
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isGold) "9999 FINE" else "999 PURE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = metalType.displayNameNp,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = metalType.displayNameEn,
                            fontSize = 13.sp,
                            color = accentColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                ChangeBadge(delta = metalInfo.delta)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading || metalInfo.rate == 0) {
                Text(
                    text = "रु ———",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White.copy(alpha = 0.3f)
                )
            } else {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "रु ",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        fontFamily = FontFamily.Serif
                    )
                    AnimatedContent(
                        targetState = animatedRate,
                        transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                        label = "rateText"
                    ) { targetRate ->
                        Text(
                            text = numberFormat.format(targetRate),
                            fontSize = 46.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Serif,
                            color = Color.White
                        )
                    }
                    Text(
                        text = " / ${selectedUnit.labelNp} (${selectedUnit.labelEn})",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(PriceUnit.TOLA, PriceUnit.TEN_GRAM, PriceUnit.GRAM, PriceUnit.OUNCE).forEach { unit ->
                    val isSelected = unit == selectedUnit
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) accentColor else Color.Transparent)
                            .clickable { selectedUnit = unit }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = unit.labelEn,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "नोट: ${metalInfo.note}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                val formatted = DateUtils.formatTimestamp(metalInfo.lastUpdated)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "मिति: ${formatted.date}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "समय: ${formatted.time}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
private fun ChangeBadge(delta: Int) {
    val (bgColor, textColor, label) = when {
        delta > 0 -> Triple(AccentGreen.copy(alpha = 0.2f), AccentGreen, "▲ +रु $delta")
        delta < 0 -> Triple(AccentRed.copy(alpha = 0.2f), AccentRed, "▼ -रु ${kotlin.math.abs(delta)}")
        else -> Triple(Color.Gray.copy(alpha = 0.2f), Color.Gray, "— Unchanged")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
