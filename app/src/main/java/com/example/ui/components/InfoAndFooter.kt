package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.MetalInfo
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InfoAndFooter(
    goldInfo: MetalInfo,
    silverInfo: MetalInfo,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

    val onShare = {
        val shareText = """
            🇳🇵 DarkRoot Gold & Silver Rate ($todayDate)
            
            🟡 Fine Gold (9999): रु ${numberFormat.format(goldInfo.rate)} / tola
            ⚪ Pure Silver (999): रु ${numberFormat.format(silverInfo.rate)} / tola
            
            Source: FENEGOSIDA (Kathmandu)
            Track live rates on DarkRoot Gold & Silver.
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Rates")
        context.startActivity(shareIntent)
    }

    val openFenegosida = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://fenegosida.org"))
        context.startActivity(intent)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Share Button
        Button(
            onClick = onShare,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0x33D4A520)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFD4A520), RoundedCornerShape(16.dp))
                .testTag("share_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color(0xFFECC840)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "आजको दर सेयर गर्नुहोस् (Share Live Rates)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info Strip
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoStripItem("स्रोत (Source)", "FENEGOSIDA")
                InfoStripItem("अपडेट (Updates)", "२ पटक / दिन (2x Daily)")
                InfoStripItem("बजार (Market)", "काठमाडौँ (Kathmandu)")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Footer Branding & Reference Link
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DhakaWeaveBar(height = 3.dp)
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "DarkRoot Fintech · Nepal Gold & Silver Tracker",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4A520)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "1 Tola = 11.664 Grams | 1 Ounce = 31.1035 Grams | 1 Lal = 0.25 Tola",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0x88FFFFFF)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { openFenegosida() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Official Source: fenegosida.org",
                    fontSize = 11.sp,
                    color = Color(0xBBFFFFFF)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "External link",
                    tint = Color(0xBBFFFFFF),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoStripItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color(0xAAFFFFFF))
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
