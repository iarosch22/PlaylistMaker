package com.practicum.playlistmaker.settings.domain

interface SettingsInteractor {
    fun getThemePreference(): Boolean

    fun saveThemePreferences(isDarkTheme: Boolean)

}