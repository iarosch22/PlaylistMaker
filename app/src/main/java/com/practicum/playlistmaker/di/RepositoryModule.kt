package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.dto.preferences.TrackManager
import com.practicum.playlistmaker.search.domain.TracksRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<TrackManager> {
        TrackManager(get())
    }

}