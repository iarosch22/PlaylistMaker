package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor

interface PlayerRepository {
    fun preparePlayer(trackUrl: String, onPreparedListener: PlayerInteractor.OnPreparedListener, onCompletedListener: PlayerInteractor.OnCompletedListener)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun getCurrentPosition(): Int
}