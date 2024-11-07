package com.practicum.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun preparePlayer(onPrepared: OnPreparedListener, onCompleted: OnCompletedListener)

    interface OnPreparedListener {
        fun onPrepared()
    }

    interface OnCompletedListener {
        fun onComplete()
    }

    fun startPlayer()

    fun pausePlayer()

    fun getCurrentPosition(): Int
}