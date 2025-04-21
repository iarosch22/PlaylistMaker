package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.domain.db.LibraryInteractor
import com.practicum.playlistmaker.library.domain.impl.LibraryInteractorImpl
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistInteractor
import com.practicum.playlistmaker.creationplaylist.domain.impl.CreationPlaylistInteractorImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<LibraryInteractor> {
        LibraryInteractorImpl(get())
    }

    single<CreationPlaylistInteractor> {
        CreationPlaylistInteractorImpl(get())
    }

}