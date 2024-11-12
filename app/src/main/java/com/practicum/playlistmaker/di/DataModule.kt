package com.practicum.playlistmaker.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.preferences.TrackManager
import com.practicum.playlistmaker.search.data.network.ItunesApi
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.settings.data.SettingsManager
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }
    single(named("app_search_history")) {
        androidContext()
            .getSharedPreferences("app_search_history", Context.MODE_PRIVATE)
    }
    factory { Gson() }
    single<TrackManager> {
        TrackManager(get(named("app_search_history")))
    }
    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }


    single(named("app_theme_preferences")) {
        androidContext()
            .getSharedPreferences("app_theme_preferences", Context.MODE_PRIVATE)
    }
    single<SettingsManager> {
        SettingsManager(get(named("app_theme_preferences")))
    }
    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }

}
