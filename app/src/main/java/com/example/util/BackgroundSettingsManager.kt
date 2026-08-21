package com.example.util

import android.content.Context
import android.content.SharedPreferences

class BackgroundSettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("background_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BACKGROUND_IMAGE = "background_image_url"
        private const val DEFAULT_IMAGE = "https://mrwallpaper.com/images/hd/time-is-money-iphone-y8u1wjneyc995bpr.jpg"
    }

    fun getBackgroundImage(): String {
        return prefs.getString(KEY_BACKGROUND_IMAGE, DEFAULT_IMAGE) ?: DEFAULT_IMAGE
    }

    fun setBackgroundImage(url: String) {
        prefs.edit().putString(KEY_BACKGROUND_IMAGE, url).apply()
    }
}
