package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val metal: String, // "GOLD" or "SILVER"
    val direction: String, // "ABOVE" or "BELOW"
    val targetPrice: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val isTriggered: Boolean = false
)
