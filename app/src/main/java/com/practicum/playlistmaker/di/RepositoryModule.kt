package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.data.LibraryRepositoryImpl
import com.practicum.playlistmaker.library.domain.db.LibraryRepository
import com.practicum.playlistmaker.creationplaylist.data.CreationPlaylistRepositoryImpl
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistRepository
import com.practicum.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<LibraryRepository> {
        LibraryRepositoryImpl(get(), get())
    }

    single<CreationPlaylistRepository> {
        CreationPlaylistRepositoryImpl(get(), get())
    }

}