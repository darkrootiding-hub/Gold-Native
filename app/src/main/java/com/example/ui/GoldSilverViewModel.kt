package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PriceAlertEntity
import com.example.data.model.MarketMood
import com.example.data.model.MarketMoodState
import com.example.data.model.MetalInfo
import com.example.data.model.MetalType
import com.example.data.model.PriceHistoryPoint
import com.example.data.repository.GoldSilverRepository
import com.example.util.BackgroundSettingsManager
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

data class GoldSilverUiState(
    val goldInfo: MetalInfo = MetalInfo(),
    val silverInfo: MetalInfo = MetalInfo(),
    val goldHistory: List<PriceHistoryPoint> = emptyList(),
    val silverHistory: List<PriceHistoryPoint> = emptyList(),
    val isGoldLoading: Boolean = true,
    val isSilverLoading: Boolean = true,
    val marketMood: MarketMood = MarketMood(),
    val backgroundImageUrl: String = ""
)

class GoldSilverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GoldSilverRepository(application)
    private val alertDao = AppDatabase.getDatabase(application).priceAlertDao()
    private val backgroundSettingsManager = BackgroundSettingsManager(application)

    private val _uiState = MutableStateFlow(GoldSilverUiState())
    val uiState: StateFlow<GoldSilverUiState> = _uiState.asStateFlow()

    val alerts: StateFlow<List<PriceAlertEntity>> = alertDao.getAllAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        _uiState.value = _uiState.value.copy(
            backgroundImageUrl = backgroundSettingsManager.getBackgroundImage()
        )
        observeGold()
        observeSilver()
    }

    // Yesterday's saved rate is read from the price history collection (the
    // permanent online record) rather than tracked on-device, so the change
    // badge can't drift because of a stale local/offline-cache read.
    private fun yesterdayRateFrom(history: List<PriceHistoryPoint>): Int? =
        if (history.size >= 2) history[history.size - 2].rate else null

    private fun observeGold() {
        viewModelScope.launch {
            combine(
                repository.observeMetalInfo("global_data/gold_info"),
                repository.observeHistory("gold_history")
            ) { info, history -> info to history }
                .collect { (info, history) ->
                    val yesterdayRate = yesterdayRateFrom(history) ?: info.previousRate
                    val updatedInfo = info.copy(previousRate = yesterdayRate)

                    _uiState.value = _uiState.value.copy(
                        goldInfo = updatedInfo,
                        goldHistory = history,
                        marketMood = calculateMarketMood(history),
                        isGoldLoading = false
                    )

                    checkAlerts(MetalType.GOLD, info.rate)
                }
        }
    }

    private fun observeSilver() {
        viewModelScope.launch {
            combine(
                repository.observeMetalInfo("global_data/silver_info"),
                repository.observeHistory("silver_history")
            ) { info, history -> info to history }
                .collect { (info, history) ->
                    val yesterdayRate = yesterdayRateFrom(history) ?: info.previousRate
                    val updatedInfo = info.copy(previousRate = yesterdayRate)

                    _uiState.value = _uiState.value.copy(
                        silverInfo = updatedInfo,
                        silverHistory = history,
                        isSilverLoading = false
                    )

                    checkAlerts(MetalType.SILVER, info.rate)
                }
        }
    }

    private fun checkAlerts(metalType: MetalType, currentRate: Int) {
        if (currentRate <= 0) return
        viewModelScope.launch {
            val activeAlerts = alerts.value
            for (alert in activeAlerts) {
                if (!alert.isTriggered && alert.metal.equals(metalType.name, ignoreCase = true)) {
                    val triggered = if (alert.direction.equals("ABOVE", ignoreCase = true)) {
                        currentRate >= alert.targetPrice
                    } else {
                        currentRate <= alert.targetPrice
                    }

                    if (triggered) {
                        alertDao.updateAlert(alert.copy(isTriggered = true))
                        val dirText = if (alert.direction.equals("ABOVE", ignoreCase = true)) "exceeded" else "dropped below"
                        NotificationHelper.triggerPriceNotification(
                            getApplication(),
                            alert.id,
                            "${metalType.displayNameEn} Price Alert!",
                            "${metalType.displayNameEn} has $dirText target NPR ${alert.targetPrice}! Current rate: NPR $currentRate"
                        )
                    }
                }
            }
        }
    }

    fun addAlert(metal: MetalType, direction: String, targetPrice: Int) {
        viewModelScope.launch {
            alertDao.insertAlert(
                PriceAlertEntity(
                    metal = metal.name,
                    direction = direction,
                    targetPrice = targetPrice
                )
            )
        }
    }

    fun deleteAlert(id: Int) {
        viewModelScope.launch {
            alertDao.deleteAlert(id)
        }
    }

    fun updateBackgroundImage(url: String) {
        backgroundSettingsManager.setBackgroundImage(url)
        _uiState.value = _uiState.value.copy(backgroundImageUrl = url)
    }

    private fun calculateMarketMood(history: List<PriceHistoryPoint>): MarketMood {
        if (history.size < 2) return MarketMood()

        val recent = history.takeLast(7)
        val firstRate = recent.first().rate
        val lastRate = recent.last().rate
        if (firstRate <= 0) return MarketMood()

        val trendPct = ((lastRate - firstRate).toDouble() / firstRate) * 100.0

        // Daily deltas
        val deltas = mutableListOf<Double>()
        for (i in 1 until recent.size) {
            deltas.add((recent[i].rate - recent[i - 1].rate).toDouble())
        }

        // Streak
        var streak = 0
        if (deltas.isNotEmpty()) {
            val lastSign = if (deltas.last() > 0) 1 else if (deltas.last() < 0) -1 else 0
            if (lastSign != 0) {
                for (i in deltas.indices.reversed()) {
                    val sign = if (deltas[i] > 0) 1 else if (deltas[i] < 0) -1 else 0
                    if (sign == lastSign) {
                        streak += sign
                    } else break
                }
            }
        }

        // Volatility (standard deviation of deltas)
        val mean = if (deltas.isNotEmpty()) deltas.average() else 0.0
        val variance = if (deltas.isNotEmpty()) deltas.sumOf { (it - mean).pow(2) } / deltas.size else 0.0
        val stdev = sqrt(variance)

        val state = when {
            trendPct > 1.5 -> MarketMoodState.BULLISH
            trendPct in 0.3..1.5 -> MarketMoodState.MILDLY_BULLISH
            trendPct in -0.3..0.3 -> MarketMoodState.NEUTRAL
            trendPct in -1.5..-0.3 -> MarketMoodState.MILDLY_BEARISH
            else -> MarketMoodState.BEARISH
        }

        return MarketMood(
            state = state,
            trendPercent = trendPct,
            streakDays = streak,
            volatility = stdev
        )
    }
}
