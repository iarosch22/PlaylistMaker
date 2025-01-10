package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerRepository

class PlayerInteractorImpl(private val repository: PlayerRepository): PlayerInteractor {
    override fun preparePlayer(
        trackUrl: String,
        onPrepared: PlayerInteractor.OnPreparedListener,
        onCompleted: PlayerInteractor.OnCompletedListener
    ) {
        repository.preparePlayer(trackUrl, onPrepared, onCompleted)
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }

    override fun getStatePlaying(): Boolean {
        return repository.getStatePlaying()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }
}