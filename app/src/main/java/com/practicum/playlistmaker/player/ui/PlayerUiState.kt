package com.practicum.playlistmaker.player.ui

sealed interface PlayerUiState {
    data object Prepared : PlayerUiState
    data object Playing: PlayerUiState
    data object Pause: PlayerUiState
    data object Default: PlayerUiState
}