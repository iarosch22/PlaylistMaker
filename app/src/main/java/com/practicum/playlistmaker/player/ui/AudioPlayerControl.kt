package com.practicum.playlistmaker.player.ui

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun getPlayerState(): StateFlow<PlayerUiState>
    fun startPlayer()
    fun pausePlayer()
    fun startNotification()
    fun stopNotification()
}