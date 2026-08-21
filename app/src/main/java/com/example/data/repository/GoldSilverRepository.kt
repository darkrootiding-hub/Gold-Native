package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.MetalInfo
import com.example.data.model.PriceHistoryPoint
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GoldSilverRepository(private val context: Context) {

    @Volatile
    private var firestoreInstance: FirebaseFirestore? = null

    private fun getFirestoreInstance(): FirebaseFirestore {
        return firestoreInstance ?: synchronized(this) {
            firestoreInstance ?: run {
                if (FirebaseApp.getApps(context).isEmpty()) {
                    try {
                        val options = FirebaseOptions.Builder()
                            .setApiKey("AIzaSyCq0m4h_K12cfGOBgU6MDOTOkFl0Iwl8qw")
                            .setApplicationId("1:880743219499:web:750a153fd1477bac4b856e")
                            .setProjectId("gold-api-ce87d")
                            .setStorageBucket("gold-api-ce87d.firebasestorage.app")
                            .setGcmSenderId("880743219499")
                            .build()
                        FirebaseApp.initializeApp(context, options)
                    } catch (e: Exception) {
                        Log.e("GoldSilverRepository", "Error initializing FirebaseApp: ${e.message}")
                    }
                }
                val db = FirebaseFirestore.getInstance()
                try {
                    val settings = FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build()
                    db.firestoreSettings = settings
                } catch (e: Exception) {
                    Log.w("GoldSilverRepository", "Firestore settings config note: ${e.message}")
                }
                db.also { firestoreInstance = it }
            }
        }
    }

    fun observeMetalInfo(documentPath: String): Flow<MetalInfo> = callbackFlow {
        val isGold = documentPath.contains("gold")
        val fallbackInfo = if (isGold) {
            MetalInfo(rate = 171000, lastUpdated = "Today 11:00 AM", source = "FENEGOSIDA", note = "Fine Gold (9999) - Tola")
        } else {
            MetalInfo(rate = 1980, lastUpdated = "Today 11:00 AM", source = "FENEGOSIDA", note = "Pure Silver (999) - Tola")
        }

        try {
            val firestore = getFirestoreInstance()
            val docRef = firestore.document(documentPath)

            val listener = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("GoldSilverRepository", "Listen failed for $documentPath: ${error.message}")
                    trySend(fallbackInfo)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val rate = snapshot.getLong("rate")?.toInt() ?: fallbackInfo.rate
                    val lastUpdated = snapshot.getString("lastUpdated") ?: fallbackInfo.lastUpdated
                    val source = snapshot.getString("source") ?: fallbackInfo.source
                    val note = snapshot.getString("note") ?: fallbackInfo.note
                    val previousRate = snapshot.getLong("previousRate")?.toInt()

                    trySend(MetalInfo(rate = rate, lastUpdated = lastUpdated, source = source, note = note, previousRate = previousRate))
                } else {
                    trySend(fallbackInfo)
                }
            }

            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Log.e("GoldSilverRepository", "Firestore connection exception: ${e.message}")
            trySend(fallbackInfo)
            awaitClose { }
        }
    }

    fun observeHistory(collectionName: String): Flow<List<PriceHistoryPoint>> = callbackFlow {
        val isGold = collectionName.contains("gold")
        val fallbackHistory = if (isGold) getFallbackGoldHistory() else getFallbackSilverHistory()

        try {
            val firestore = getFirestoreInstance()
            val query = firestore.collection(collectionName)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(14)

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("GoldSilverRepository", "History listen failed for $collectionName: ${error.message}")
                    trySend(fallbackHistory)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val points = snapshot.documents.mapNotNull { doc ->
                        val rate = doc.getLong("rate")?.toInt()
                        val timestamp = doc.getString("timestamp") ?: ""
                        if (rate != null) PriceHistoryPoint(rate = rate, timestamp = timestamp) else null
                    }
                    trySend(points.reversed())
                } else {
                    trySend(fallbackHistory)
                }
            }

            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Log.e("GoldSilverRepository", "History connection exception: ${e.message}")
            trySend(fallbackHistory)
            awaitClose { }
        }
    }

    private fun getFallbackGoldHistory(): List<PriceHistoryPoint> {
        return listOf(
            PriceHistoryPoint(167500, "2026-07-27"),
            PriceHistoryPoint(168000, "2026-07-28"),
            PriceHistoryPoint(167800, "2026-07-29"),
            PriceHistoryPoint(168500, "2026-07-30"),
            PriceHistoryPoint(169000, "2026-07-31"),
            PriceHistoryPoint(168800, "2026-08-01"),
            PriceHistoryPoint(169500, "2026-08-02"),
            PriceHistoryPoint(170000, "2026-08-03"),
            PriceHistoryPoint(169800, "2026-08-04"),
            PriceHistoryPoint(170200, "2026-08-05"),
            PriceHistoryPoint(170500, "2026-08-06"),
            PriceHistoryPoint(170800, "2026-08-07"),
            PriceHistoryPoint(170600, "2026-08-08"),
            PriceHistoryPoint(171000, "2026-08-09")
        )
    }

    private fun getFallbackSilverHistory(): List<PriceHistoryPoint> {
        return listOf(
            PriceHistoryPoint(1920, "2026-07-27"),
            PriceHistoryPoint(1930, "2026-07-28"),
            PriceHistoryPoint(1925, "2026-07-29"),
            PriceHistoryPoint(1940, "2026-07-30"),
            PriceHistoryPoint(1950, "2026-07-31"),
            PriceHistoryPoint(1945, "2026-08-01"),
            PriceHistoryPoint(1955, "2026-08-02"),
            PriceHistoryPoint(1960, "2026-08-03"),
            PriceHistoryPoint(1965, "2026-08-04"),
            PriceHistoryPoint(1970, "2026-08-05"),
            PriceHistoryPoint(1968, "2026-08-06"),
            PriceHistoryPoint(1975, "2026-08-07"),
            PriceHistoryPoint(1978, "2026-08-08"),
            PriceHistoryPoint(1980, "2026-08-09")
        )
    }
}

