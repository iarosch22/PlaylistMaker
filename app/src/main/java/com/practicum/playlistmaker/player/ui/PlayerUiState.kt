package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist

sealed class PlayerUiState {

    object Default : PlayerUiState()

    object Prepared : PlayerUiState()

    class AddingToPlaylist(val playlistName:String, val isSuccess: Boolean): PlayerUiState()

    data class Playing(val progress: String, val isFavorite: Boolean) : PlayerUiState()

    data class Paused(val progress: String, val isFavorite: Boolean) : PlayerUiState()

}