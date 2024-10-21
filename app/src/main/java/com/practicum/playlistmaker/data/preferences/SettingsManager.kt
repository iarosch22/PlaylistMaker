package com.practicum.playlistmaker.data.preferences

import android.app.Application.MODE_PRIVATE
import android.content.Context

const val APP_THEME_PREFERENCES = "app_theme_preferences"
const val DARK_THEME = "dark_theme"

class SettingsManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(APP_THEME_PREFERENCES, MODE_PRIVATE)


    fun getThemePreference(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME, false)
    }

    fun saveThemePreferences(isDarkTheme: Boolean) {
        sharedPreferences.edit()
            .putBoolean(com.practicum.playlistmaker.ui.DARK_THEME, isDarkTheme)
            .apply()
    }

}