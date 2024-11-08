package com.practicum.playlistmaker.player.data

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor

interface PlayerRepository {
    fun preparePlayer(onPreparedListener: PlayerInteractor.OnPreparedListener, onCompletedListener: PlayerInteractor.OnCompletedListener)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun getCurrentPosition(): Int
}