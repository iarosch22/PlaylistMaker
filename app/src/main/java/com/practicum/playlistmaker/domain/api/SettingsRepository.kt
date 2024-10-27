package com.practicum.playlistmaker.domain.api

interface SettingsRepository {
    fun getThemePreference(): Boolean

    fun saveThemePreferences(isDarkTheme: Boolean)
}