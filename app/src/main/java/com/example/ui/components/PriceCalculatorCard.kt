package com.example.ui.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MetalInfo
import com.example.data.model.MetalType
import com.example.data.model.PriceUnit
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PriceCalculatorCard(
    goldInfo: MetalInfo,
    silverInfo: MetalInfo,
    modifier: Modifier = Modifier
) {
    var selectedMetal by remember { mutableStateOf(MetalType.GOLD) }
    var quantityText by remember { mutableStateOf("1") }
    var selectedUnit by remember { mutableStateOf(PriceUnit.TOLA) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val activeRate = if (selectedMetal == MetalType.GOLD) goldInfo.rate else silverInfo.rate

    val quantity = quantityText.toDoubleOrNull() ?: 0.0
    // Rate per gram = activeRate / 11.664
    val totalGramWeight = quantity * selectedUnit.gramsPerUnit
    val totalNpr = (totalGramWeight / 11.664) * activeRate

    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("calculator_card"),
        cornerRadius = 28.dp,
        glowColor = if (selectedMetal == MetalType.GOLD) Color(0x33D4A520) else Color(0x338094A8)
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
                        text = "मूल्य गणना (Price Calculator)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Instant NPR estimate for any weight",
                        fontSize = 11.sp,
                        color = Color(0xBBFFFFFF)
                    )
                }

                // Metal Toggle
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
                            .clickable { selectedMetal = MetalType.GOLD }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("calculator_gold_toggle")
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
                            .clickable { selectedMetal = MetalType.SILVER }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("calculator_silver_toggle")
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

            Spacer(modifier = Modifier.height(16.dp))

            // Preset Weight Quick Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("0.5", "1", "2", "5", "10", "100").forEach { preset ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (quantityText == preset) Color(0x40FFFFFF) else Color(0x15000000))
                            .border(1.dp, if (quantityText == preset) Color(0xFFD4A520) else Color(0x1AFFFFFF), RoundedCornerShape(10.dp))
                            .clickable { quantityText = preset }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quantity Input & Unit Dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Quantity (मात्रा)", color = Color(0xAAFFFFFF)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD4A520),
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("calculator_quantity_input")
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Unit Selector Dropdown
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x20FFFFFF))
                            .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(12.dp))
                            .clickable { dropdownExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 14.dp)
                            .testTag("calculator_unit_dropdown")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${selectedUnit.labelNp} (${selectedUnit.labelEn})",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select unit",
                                tint = Color.White
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        PriceUnit.entries.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text("${unit.labelNp} (${unit.labelEn})") },
                                onClick = {
                                    selectedUnit = unit
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Calculated Output Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x22000000))
                    .border(1.2.dp, Color(0x33FFFFFF), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "अनुमानित कुल रकम (Total Estimated Value)",
                        fontSize = 11.sp,
                        color = Color(0xBBFFFFFF)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "रु ${numberFormat.format(totalNpr.toInt())}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        color = if (selectedMetal == MetalType.GOLD) Color(0xFFECC840) else Color(0xFFBED0E0)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Equivalent Weight: ${NumberFormat.getInstance().format(totalGramWeight)} Grams",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xAAFFFFFF)
                    )
                }
            }
        }
    }
}
