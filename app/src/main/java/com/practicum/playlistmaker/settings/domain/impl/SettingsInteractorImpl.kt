package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository): SettingsInteractor {
    override fun getThemePreference(): Boolean {
        return repository.getThemePreference()
    }

    override fun saveThemePreferences(isDarkTheme: Boolean) {
        repository.saveThemePreferences(isDarkTheme)
    }
}