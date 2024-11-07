package com.practicum.playlistmaker.player.presentation

sealed interface PlayerUiState {
    data object Prepared : PlayerUiState
    data object Playing: PlayerUiState
    data object Pause: PlayerUiState
    data object Default: PlayerUiState
}