package com.practicum.playlistmaker.player.domain.api

interface PlayerRepository {
    fun preparePlayer(trackUrl: String, onPreparedListener: PlayerInteractor.OnPreparedListener, onCompletedListener: PlayerInteractor.OnCompletedListener)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun getStatePlaying(): Boolean

    fun getCurrentPosition(): Int
}