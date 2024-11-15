package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val app: App,
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {


//    companion object {
//        fun getViewModelFactory(app: App, sharingInteractor: SharingInteractor, settingsInteractor: SettingsInteractor) : ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                SettingsViewModel(app, sharingInteractor, settingsInteractor)
//            }
//        }
//    }

    fun getThemePreference(): Boolean {
        return settingsInteractor.getThemePreference()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        app.switchTheme(darkThemeEnabled)
        settingsInteractor.saveThemePreferences(darkThemeEnabled)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }
}