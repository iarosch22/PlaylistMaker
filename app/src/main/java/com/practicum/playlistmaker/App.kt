package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator

class App: Application() {

    companion object {
        private lateinit var appContext: App

        fun getAppContext(): Context {
            return appContext.applicationContext
        }
    }

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        appContext = this

        val settingsInteractor = Creator.provideSettingsInteractor()

        darkTheme = settingsInteractor.getThemePreference()

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}