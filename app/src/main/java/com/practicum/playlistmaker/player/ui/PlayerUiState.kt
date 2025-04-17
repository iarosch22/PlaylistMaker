package com.practicum.playlistmaker.player.ui

sealed class PlayerUiState(val progress: String, var isFavorite: Boolean) {

    class Default(isFavorite: Boolean) : PlayerUiState("00:00", isFavorite)

    class Prepared(isFavorite: Boolean) : PlayerUiState("00:00", isFavorite)

    class Playing(progress: String, isFavorite: Boolean) : PlayerUiState(progress, isFavorite)

    class Paused(progress: String, isFavorite: Boolean) : PlayerUiState(progress, isFavorite)

    class BottomSheetShowing()

}