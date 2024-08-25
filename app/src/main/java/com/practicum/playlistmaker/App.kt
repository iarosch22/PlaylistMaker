package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val APP_THEME_PREFERENCES = "app_theme_preferences"
const val DARK_THEME = "dark_theme"

class App: Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val preferences = getSharedPreferences(APP_THEME_PREFERENCES, MODE_PRIVATE)
        darkTheme = preferences.getBoolean(DARK_THEME, false)

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}