package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.PlayerUiState

class PlayerViewModel(trackUrl: String, private val playerInteractor: PlayerInteractor): ViewModel() {

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
                    updateState(PlayerUiState.Default)
                }
            }
        )
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        updateState(PlayerUiState.Playing)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        updateState(PlayerUiState.Pause)
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
    }

    fun getCurrentPosition(): Int {
        return playerInteractor.getCurrentPosition()
    }

    private fun updateState(state: PlayerUiState) {
        stateLiveData.postValue(state)
    }

}