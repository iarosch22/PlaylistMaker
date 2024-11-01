package com.practicum.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun getThemePreference(): Boolean

    fun saveThemePreferences(isDarkTheme: Boolean)
}