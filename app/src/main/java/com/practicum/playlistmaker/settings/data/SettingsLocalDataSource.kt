package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences

const val APP_THEME_PREFERENCES = "app_theme_preferences"
const val DARK_THEME = "dark_theme"

class  SettingsLocalDataSource(private val sharedPreferences: SharedPreferences) {

    //private val sharedPreferences = context.getSharedPreferences(APP_THEME_PREFERENCES, MODE_PRIVATE)


    fun getThemePreference(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME, false)
    }

    fun saveThemePreferences(isDarkTheme: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME, isDarkTheme)
            .apply()
    }

}