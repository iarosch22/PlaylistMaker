package com.practicum.playlistmaker.settings.data.impl

import com.practicum.playlistmaker.settings.data.SettingsManager
import com.practicum.playlistmaker.settings.data.SettingsRepository

class SettingsRepositoryImpl(private val settingsManager: SettingsManager): SettingsRepository {
    override fun getThemePreference(): Boolean {
        return settingsManager.getThemePreference()
    }

    override fun saveThemePreferences(isDarkTheme: Boolean) {
        settingsManager.saveThemePreferences(isDarkTheme)
    }
}