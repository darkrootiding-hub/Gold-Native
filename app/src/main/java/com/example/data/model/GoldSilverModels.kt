package com.example.data.model

import androidx.annotation.Keep

@Keep
enum class MetalType(val displayNameEn: String, val displayNameNp: String, val subTitleEn: String) {
    GOLD("Fine Gold", "सुनको भाउ", "Hallmark 9999"),
    SILVER("Pure Silver", "चाँदीको भाउ", "Fine Silver 999")
}

@Keep
data class MetalInfo(
    val rate: Int = 0,
    val lastUpdated: String = "",
    val source: String = "FENEGOSIDA",
    val note: String = "",
    val previousRate: Int? = null
) {
    val delta: Int
        get() = if (previousRate != null) rate - previousRate else 0

    val perGram: Double
        get() = rate / 11.664

    val per10g: Double
        get() = perGram * 10.0

    val perOunce: Double
        get() = perGram * 31.1035
}

@Keep
data class PriceHistoryPoint(
    val rate: Int = 0,
    val timestamp: String = ""
)

enum class PriceUnit(
    val labelEn: String,
    val labelNp: String,
    val gramsPerUnit: Double
) {
    TOLA("Tola", "तोला", 11.664),
    GRAM("Gram", "ग्राम", 1.0),
    TEN_GRAM("10 Gram", "१० ग्राम", 10.0),
    OUNCE("Ounce (oz)", "अउन्स", 31.1035),
    KILOGRAM("Kilogram (kg)", "किलोग्राम", 1000.0),
    LAL("Lal (लाल)", "लाल", 2.916) // 1 tola = 4 lal, so 11.664 / 4 = 2.916g
}

enum class AlertDirection(val labelEn: String, val labelNp: String) {
    ABOVE("Above", "माथि"),
    BELOW("Below", "तल्लो")
}

enum class MarketMoodState(val labelEn: String, val labelNp: String, val emoji: String) {
    BULLISH("Bullish", "तीव्र वृद्धि", "🔥"),
    MILDLY_BULLISH("Mildly Bullish", "सामान्य वृद्धि", "📈"),
    NEUTRAL("Neutral", "स्थिर", "⚖️"),
    MILDLY_BEARISH("Mildly Bearish", "सामान्य गिरावट", "📉"),
    BEARISH("Bearish", "तीव्र गिरावट", "❄️")
}

data class MarketMood(
    val state: MarketMoodState = MarketMoodState.NEUTRAL,
    val trendPercent: Double = 0.0,
    val streakDays: Int = 0,
    val volatility: Double = 0.0
)
