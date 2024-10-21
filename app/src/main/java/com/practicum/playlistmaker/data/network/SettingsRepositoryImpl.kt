package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.preferences.SettingsManager
import com.practicum.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val settingsManager: SettingsManager): SettingsRepository {
    override fun getThemePreference(): Boolean {
        return settingsManager.getThemePreference()
    }

    override fun saveThemePreferences(isDarkTheme: Boolean) {
        settingsManager.saveThemePreferences(isDarkTheme)
    }
}