package com.practicum.playlistmaker.settings.data.impl

import com.practicum.playlistmaker.settings.data.SettingsLocalDataSource
import com.practicum.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl(private val settingsLocalDataSource: SettingsLocalDataSource): SettingsRepository {
    override fun getThemePreference(): Boolean {
        return settingsLocalDataSource.getThemePreference()
    }

    override fun saveThemePreferences(isDarkTheme: Boolean) {
        settingsLocalDataSource.saveThemePreferences(isDarkTheme)
    }
}