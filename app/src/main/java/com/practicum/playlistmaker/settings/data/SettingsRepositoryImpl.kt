package com.practicum.playlistmaker.settings.data

import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val settingsManager: SettingsManager): SettingsRepository {
    override fun getThemePreference(): Boolean {
        return settingsManager.getThemePreference()
    }

    override fun saveThemePreferences(isDarkTheme: Boolean) {
        settingsManager.saveThemePreferences(isDarkTheme)
    }
}