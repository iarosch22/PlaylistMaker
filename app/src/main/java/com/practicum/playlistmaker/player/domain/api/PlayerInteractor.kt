package com.practicum.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun preparePlayer()

    fun startPlayer()

    fun pausePlayer()

    fun getCurrentPosition(): Int
}