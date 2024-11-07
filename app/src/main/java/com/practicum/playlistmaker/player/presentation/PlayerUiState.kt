package com.practicum.playlistmaker.player.presentation

sealed interface PlayerUiState {
    data object Prepared : PlayerUiState
    data object Completed: PlayerUiState
    data object Playing: PlayerUiState
    data object Pause: PlayerUiState
    data object Default: PlayerUiState
    data class Error(val message: String): PlayerUiState
}