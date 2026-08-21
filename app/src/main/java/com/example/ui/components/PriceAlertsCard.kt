package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.local.PriceAlertEntity
import com.example.data.model.AlertDirection
import com.example.data.model.MetalType
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PriceAlertsCard(
    alerts: List<PriceAlertEntity>,
    onAddAlert: (MetalType, String, Int) -> Unit,
    onDeleteAlert: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedMetal by remember { mutableStateOf(MetalType.GOLD) }
    var selectedDirection by remember { mutableStateOf(AlertDirection.ABOVE) }
    var targetPriceText by remember { mutableStateOf("") }

    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    // Permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permission result handled
    }

    val requestNotificationPermissionIfNeeded = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("price_alerts_card"),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alerts",
                        tint = Color(0xFFD4A520)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "मूल्य सूचना (Price Alerts)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Get real Android notifications when target rate is hit",
                            fontSize = 11.sp,
                            color = Color(0xAAFFFFFF)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggles Row: Metal & Direction
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metal Selector
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
                            .padding(horizontal = 10.dp, vertical = 6.dp)
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
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Silver",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedMetal == MetalType.SILVER) Color.Black else Color.White
                        )
                    }
                }

                // Direction Selector
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22000000))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                        .padding(3.dp)
                ) {
                    AlertDirection.entries.forEach { dir ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedDirection == dir) Color(0x40FFFFFF) else Color.Transparent)
                                .clickable { selectedDirection = dir }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${dir.labelNp} (${dir.labelEn})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Target Price Input + Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = targetPriceText,
                    onValueChange = { targetPriceText = it },
                    label = { Text("Target NPR Rate", color = Color(0xAAFFFFFF)) },
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
                        .testTag("alert_target_price_input")
                )

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = {
                        val price = targetPriceText.toIntOrNull()
                        if (price != null && price > 0) {
                            requestNotificationPermissionIfNeeded()
                            onAddAlert(selectedMetal, selectedDirection.name, price)
                            targetPriceText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4A520)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .testTag("set_alert_button")
                ) {
                    Text("Set Alert", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Alerts List
            if (alerts.isEmpty()) {
                Text(
                    text = "No active price alerts set.",
                    fontSize = 12.sp,
                    color = Color(0x77FFFFFF)
                )
            } else {
                Text(
                    text = "Active Alerts (${alerts.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xDDFFFFFF)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    alerts.forEach { alert ->
                        AlertItemRow(
                            alert = alert,
                            numberFormat = numberFormat,
                            onDelete = { onDeleteAlert(alert.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertItemRow(
    alert: PriceAlertEntity,
    numberFormat: NumberFormat,
    onDelete: () -> Unit
) {
    val isGold = alert.metal.equals("GOLD", ignoreCase = true)
    val accentColor = if (isGold) Color(0xFFD4A520) else Color(0xFF8094A8)
    val dirText = if (alert.direction.equals("ABOVE", ignoreCase = true)) "▲ Above" else "▼ Below"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x1A000000))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (alert.isTriggered) Color.Red else accentColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${if (isGold) "Gold" else "Silver"} $dirText",
                fontSize = 12.sp,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "रु ${numberFormat.format(alert.targetPrice)}",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            if (alert.isTriggered) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "(TRIGGERED)",
                    fontSize = 10.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete alert",
                tint = Color(0x88FFFFFF)
            )
        }
    }
}
