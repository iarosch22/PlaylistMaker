package com.practicum.playlistmaker.settings.domain

interface SettingsRepository {
    fun getThemePreference(): Boolean

    fun saveThemePreferences(isDarkTheme: Boolean)
}