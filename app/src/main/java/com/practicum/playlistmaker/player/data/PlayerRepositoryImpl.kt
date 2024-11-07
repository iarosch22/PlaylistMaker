package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(private val trackUrl: String): PlayerRepository {

    private val mediaPlayer = MediaPlayer()

    override fun preparePlayer(onPreparedListener: PlayerInteractor.OnPreparedListener, onCompletedListener: PlayerInteractor.OnCompletedListener) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.setOnPreparedListener{
            onPreparedListener.onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletedListener.onComplete()
        }
        mediaPlayer.prepareAsync()

    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

}