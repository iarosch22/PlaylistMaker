package com.practicum.playlistmaker.settings.data

interface SettingsRepository {
    fun getThemePreference(): Boolean

    fun saveThemePreferences(isDarkTheme: Boolean)
}