package com.practicum.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun preparePlayer(trackUrl: String, onPrepared: OnPreparedListener, onCompleted: OnCompletedListener)

    interface OnPreparedListener {
        fun onPrepared()
    }

    interface OnCompletedListener {
        fun onComplete()
    }

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun getStatePlaying(): Boolean

    fun getCurrentPosition(): Int
}