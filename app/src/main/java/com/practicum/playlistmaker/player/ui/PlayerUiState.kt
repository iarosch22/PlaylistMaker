package com.practicum.playlistmaker.player.ui

sealed interface PlayerUiState {
    data object Prepared : PlayerUiState
    data class Playing(val trackTime: Int = 0): PlayerUiState
    data object Pause: PlayerUiState
    data object Default: PlayerUiState
}