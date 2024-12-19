package com.practicum.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.PlayerUiState

class PlayerViewModel(trackUrl: String, private val playerInteractor: PlayerInteractor): ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<PlayerUiState>()
    fun observeState(): LiveData<PlayerUiState> = stateLiveData

    private val runnableDuration = object : Runnable {
            override fun run() {
                val currentState = stateLiveData.value
                if(currentState is PlayerUiState.Playing) {
                    val currentTime = getCurrentPosition()
                    updateState(currentState.copy(trackTime = currentTime))
                    handler.postDelayed(this, CHECK_INTERVAL)
                } else {
                    handler.removeCallbacks(this)
                }
            }
    }

    init {
        playerInteractor.preparePlayer(
            trackUrl,
            onPrepared = object : PlayerInteractor.OnPreparedListener {
                override fun onPrepared() {
                    updateState(PlayerUiState.Prepared)
                }
            },
            onCompleted = object : PlayerInteractor.OnCompletedListener {
                override fun onComplete() {
                    handler.removeCallbacks(runnableDuration)
                    updateState(PlayerUiState.Default)
                }
            }
        )
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        updateState(PlayerUiState.Playing(trackTime = getCurrentPosition()))
        handler.post(runnableDuration)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        handler.removeCallbacks(runnableDuration)
        updateState(PlayerUiState.Pause)
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
        handler.removeCallbacks(runnableDuration)
    }

    fun getCurrentPosition(): Int {
         return playerInteractor.getCurrentPosition()
    }

    private fun updateState(state: PlayerUiState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val CHECK_INTERVAL = 300L
    }

}