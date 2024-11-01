package com.practicum.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun getThemePreference(): Boolean

    fun saveThemePreferences(isDarkTheme: Boolean)
}