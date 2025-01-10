package com.practicum.playlistmaker.player.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.PlayerUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(trackUrl: String, private val playerInteractor: PlayerInteractor): ViewModel() {

    private var timerJob: Job? = null

    private val stateLiveData = MutableLiveData<PlayerUiState>()
    fun observeState(): LiveData<PlayerUiState> = stateLiveData

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
                    Log.d("PlayerViewModel", "Playback completed")
                    timerJob?.cancel()
                    pausePlayer()
                    updateState(PlayerUiState.Default)
                }
            }
        )
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        updateState(PlayerUiState.Playing(trackTime = playerInteractor.getCurrentPosition()))
        startTimer()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        timerJob?.cancel()
        updateState(PlayerUiState.Pause)
        Log.d("PlayerViewModel", "Player state updated to Pause")
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerInteractor.getStatePlaying()) {
                delay(CHECK_INTERVAL)
                updateState(PlayerUiState.Playing(playerInteractor.getCurrentPosition()))
            }
        }
    }

    private fun updateState(state: PlayerUiState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val CHECK_INTERVAL = 300L
    }

}