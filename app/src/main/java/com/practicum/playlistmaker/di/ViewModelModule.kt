package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.aboutplaylist.ui.view_model.AboutPlaylistViewModel
import com.practicum.playlistmaker.library.ui.favorite.view_model.FavoriteViewModel
import com.practicum.playlistmaker.library.ui.playlists.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.creationplaylist.ui.view_model.CreationPlaylistViewModel
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
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

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get())
    }

    viewModel {
        FavoriteViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel { (playlistId: Long) ->
        CreationPlaylistViewModel(playlistId, get())
    }

    viewModel { (playlistId: Long) ->
        AboutPlaylistViewModel(playlistId, get())
    }

}