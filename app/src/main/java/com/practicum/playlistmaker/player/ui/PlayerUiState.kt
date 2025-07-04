package com.practicum.playlistmaker.player.ui

sealed class PlayerUiState {

    object Default : PlayerUiState()

    object Prepared : PlayerUiState()

    object Completed: PlayerUiState()

    class AddingToPlaylist(val playlistName:String, val isSuccess: Boolean): PlayerUiState()

    data class Playing(val progress: String) : PlayerUiState()

    data class Paused(val progress: String) : PlayerUiState()

}