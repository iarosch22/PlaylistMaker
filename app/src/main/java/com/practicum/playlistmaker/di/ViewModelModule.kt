package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.ui.view_models.FavoriteViewModel
import com.practicum.playlistmaker.library.ui.view_models.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.TracksSearchViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModule = module {

    viewModel {
        TracksSearchViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get(), get())
    }

    viewModel { (trackUrl: String) ->
        PlayerViewModel(trackUrl, get())
    }

    viewModel {
        FavoriteViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }

}