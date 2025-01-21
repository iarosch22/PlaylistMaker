package com.practicum.playlistmaker.player.ui

sealed class PlayerUiState(val isPlayButtonEnabled: Boolean, val progress: String) {

    class Default : PlayerUiState(false, "00:00")

    class Prepared : PlayerUiState(true, "00:00")

    class Playing(progress: String) : PlayerUiState(true, progress)

    class Paused(progress: String) : PlayerUiState(true, progress)

}