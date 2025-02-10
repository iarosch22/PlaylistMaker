package com.practicum.playlistmaker.player.ui

sealed class PlayerUiState(val progress: String) {

    class Default : PlayerUiState("00:00")

    class Prepared : PlayerUiState("00:00")

    class Playing(progress: String) : PlayerUiState(progress)

    class Paused(progress: String) : PlayerUiState(progress)

}