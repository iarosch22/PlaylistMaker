package com.practicum.playlistmaker.di

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.library.data.converters.TrackDbConvertor
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.MIGRATION_1_TO_2
import com.practicum.playlistmaker.creationplaylist.data.converters.NewPlaylistDbConvertor
import com.practicum.playlistmaker.library.data.db.MIGRATION_2_TO_3
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.preferences.SearchHistoryLocalDataSource
import com.practicum.playlistmaker.search.data.network.ItunesApi
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.settings.data.SettingsLocalDataSource
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
    single<SearchHistoryLocalDataSource> {
        SearchHistoryLocalDataSource(get(named("app_search_history")), get())
    }
    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single { get<Application>() as App }
    single {
        androidContext()
            .getSharedPreferences("app_theme_preferences", Context.MODE_PRIVATE)
    }
    single<SettingsLocalDataSource> {
        SettingsLocalDataSource(get())
    }
    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }

    factory { MediaPlayer() }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .addMigrations(MIGRATION_1_TO_2)
            .addMigrations(MIGRATION_2_TO_3)
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        TrackDbConvertor()
    }

    single {
        NewPlaylistDbConvertor()
    }

}
