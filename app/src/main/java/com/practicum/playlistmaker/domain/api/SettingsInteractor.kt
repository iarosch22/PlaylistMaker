package com.practicum.playlistmaker.domain.api

interface SettingsInteractor {
    fun getThemePreference(): Boolean

    fun saveThemePreferences(isDarkTheme: Boolean)
}