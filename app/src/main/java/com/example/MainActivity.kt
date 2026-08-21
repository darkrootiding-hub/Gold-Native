package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.GoldSilverViewModel
import com.example.ui.components.HeaderBar
import com.example.ui.components.HeroPriceCard
import com.example.ui.components.HistoryChartCard
import com.example.ui.components.InfoAndFooter
import com.example.ui.components.MarketMoodCard
import com.example.ui.components.AppBackdrop
import com.example.ui.components.PriceAlertsCard
import com.example.ui.components.PriceCalculatorCard
import com.example.ui.components.SettingsSheet
import com.example.ui.components.UnitConverterCard
import com.example.data.model.MetalType
import com.example.ui.theme.DarkRootTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GoldSilverViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DarkRootTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val alerts by viewModel.alerts.collectAsStateWithLifecycle()

                var showSettings by remember { mutableStateOf(false) }
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

                Box(modifier = Modifier.fillMaxSize()) {
                    // User-customizable Backdrop
                    AppBackdrop(imageUrl = uiState.backgroundImageUrl)

                    // Scrollable Main Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(
                                top = statusBarPadding + 8.dp,
                                bottom = navBarPadding + 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Bar
                        HeaderBar(
                            onOpenSettings = { showSettings = true }
                        )

                        // Gold Hero Card
                        HeroPriceCard(
                            metalType = MetalType.GOLD,
                            metalInfo = uiState.goldInfo,
                            isLoading = uiState.isGoldLoading
                        )

                        // Silver Hero Card
                        HeroPriceCard(
                            metalType = MetalType.SILVER,
                            metalInfo = uiState.silverInfo,
                            isLoading = uiState.isSilverLoading
                        )

                        // Price Calculator Card
                        PriceCalculatorCard(
                            goldInfo = uiState.goldInfo,
                            silverInfo = uiState.silverInfo
                        )

                        // Price History Chart Card
                        HistoryChartCard(
                            goldHistory = uiState.goldHistory,
                            silverHistory = uiState.silverHistory
                        )

                        // Unit Converter Card
                        UnitConverterCard()

                        // Price Alerts Card
                        PriceAlertsCard(
                            alerts = alerts,
                            onAddAlert = { metal, direction, targetPrice ->
                                viewModel.addAlert(metal, direction, targetPrice)
                            },
                            onDeleteAlert = { id ->
                                viewModel.deleteAlert(id)
                            }
                        )

                        // Market Mood Card
                        MarketMoodCard(
                            mood = uiState.marketMood
                        )

                        // Info Strip & Footer
                        InfoAndFooter(
                            goldInfo = uiState.goldInfo,
                            silverInfo = uiState.silverInfo
                        )
                    }

                    // Settings Bottom Sheet
                    if (showSettings) {
                        SettingsSheet(
                            onDismiss = { showSettings = false },
                            sheetState = sheetState,
                            onUpdateBackground = { url -> viewModel.updateBackgroundImage(url) },
                            currentBackground = uiState.backgroundImageUrl
                        )
                    }
                }
            }
        }
    }
}
