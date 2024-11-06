package com.practicum.playlistmaker.player.domain.api

interface PlayerRepository {
    fun preparePlayer()

    fun startPlayer()

    fun pausePlayer()

    fun getCurrentPosition(): Int
}