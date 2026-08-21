package com.example.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    onUpdateBackground: (String) -> Unit,
    currentBackground: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    var customUrl by remember { mutableStateOf("") }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            // GetContent() only grants temporary read access that dies with the
            // process, which is why a picked photo would silently disappear
            // after restarting the app. OpenDocument() lets us persist the
            // grant so it survives forever until the user picks a new image.
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Provider doesn't support persistable permissions; the image
                // will still work for this session.
            }
            onUpdateBackground(it.toString())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF100A18),
        scrimColor = Color(0x99000000),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .testTag("settings_bottom_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "सेटिङहरू (Settings)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close settings",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            DhakaWeaveBar(height = 3.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // Notification Permission Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x1AFFFFFF))
                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFFD4A520)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Price Notifications",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (hasNotificationPermission) "System notifications enabled" else "Permission required for price alerts",
                                fontSize = 11.sp,
                                color = Color(0xAAFFFFFF)
                            )
                        }
                    }

                    if (hasNotificationPermission) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Permission granted",
                            tint = Color(0xFF10B981)
                        )
                    } else {
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4A520)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Enable", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Background Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x1AFFFFFF))
                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Background",
                                tint = Color(0xFFD4A520)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Background Image",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // System Presets
                    Text(
                        text = "System Presets",
                        fontSize = 12.sp,
                        color = Color(0xAAFFFFFF),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf(
                            "Default" to "https://mrwallpaper.com/images/hd/time-is-money-iphone-y8u1wjneyc995bpr.jpg",
                            "Gold" to "https://images.unsplash.com/photo-1610375461246-83df859662c3",
                            "Abstract" to "https://images.unsplash.com/photo-1551033406-6796666762f7"
                        )
                        presets.forEach { (name, url) ->
                            OutlinedButton(
                                onClick = { onUpdateBackground(url) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (currentBackground == url) Color(0xFFD4A520) else Color.White,
                                    containerColor = if (currentBackground == url) Color(0x22D4A520) else Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Custom URL
                    Text(
                        text = "Custom Link",
                        fontSize = 12.sp,
                        color = Color(0xAAFFFFFF),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = customUrl,
                            onValueChange = { customUrl = it },
                            label = { Text("Enter Image URL", color = Color(0xAAFFFFFF)) },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD4A520),
                                unfocusedBorderColor = Color(0x44FFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = Color(0xFFD4A520),
                                unfocusedLabelColor = Color(0xAAFFFFFF)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { if (customUrl.isNotBlank()) onUpdateBackground(customUrl) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4A520)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(Icons.Default.Link, contentDescription = "Apply", tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Device Photo
                    Text(
                        text = "Device Storage",
                        fontSize = 12.sp,
                        color = Color(0xAAFFFFFF),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedButton(
                        onClick = { photoLauncher.launch(arrayOf("image/*")) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "Gallery", tint = Color(0xFFD4A520), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose from Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x1AFFFFFF))
                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Currency & Unit Standard",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Prices are reported in Nepalese Rupees (NPR) per Tola (11.664g). Source rates updated twice daily by FENEGOSIDA (Federation of Nepal Gold & Silver Dealers' Association).",
                        fontSize = 12.sp,
                        color = Color(0xBBFFFFFF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x1AFFFFFF))
                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "About DarkRoot Gold & Silver",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4A520)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Designed & built by DarkRoot. Version 1.0.0. Live real-time Firestore synchronization with native system price alert dispatch.",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xAAFFFFFF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
