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
import com.example.data.model.PriceUnit
import java.text.DecimalFormat

@Composable
fun UnitConverterCard(modifier: Modifier = Modifier) {
    var amountText by remember { mutableStateOf("1") }
    var sourceUnit by remember { mutableStateOf(PriceUnit.TOLA) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val gramsInInput = amount * sourceUnit.gramsPerUnit

    val decimalFormat = DecimalFormat("#,##0.####")

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("unit_converter_card"),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "इकाई रूपान्तरण (Unit Converter)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Convert weight across Tola, Gram, Ounce, Lal & Kilogram",
                fontSize = 11.sp,
                color = Color(0xAAFFFFFF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount", color = Color(0xAAFFFFFF)) },
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
                        .testTag("converter_amount_input")
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x20FFFFFF))
                            .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(8.dp))
                            .clickable { dropdownExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 14.dp)
                            .testTag("converter_unit_dropdown")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sourceUnit.labelEn,
                                fontSize = 13.sp,
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
                                    sourceUnit = unit
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Equivalents Grid Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PriceUnit.entries.chunked(2).forEach { rowUnits ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowUnits.forEach { unit ->
                            val valueInUnit = gramsInInput / unit.gramsPerUnit
                            ConverterChip(
                                unitNp = unit.labelNp,
                                unitEn = unit.labelEn,
                                value = decimalFormat.format(valueInUnit),
                                isSelected = unit == sourceUnit,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConverterChip(
    unitNp: String,
    unitEn: String,
    value: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color(0x33D4A520) else Color(0x18000000))
            .border(
                1.dp,
                if (isSelected) Color(0xFFD4A520) else Color(0x22FFFFFF),
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = "$unitNp ($unitEn)",
                fontSize = 11.sp,
                color = if (isSelected) Color(0xFFECC840) else Color(0xAAFFFFFF),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color.White
            )
        }
    }
}
