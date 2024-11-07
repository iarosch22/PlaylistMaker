package com.practicum.playlistmaker.player.presentation.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.PlayerUiState

class PlayerViewModel(private val trackUrl: String): ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerUiState>()
    fun observeState(): LiveData<PlayerUiState> = stateLiveData

    private val playerInteractor: PlayerInteractor by lazy {
        Creator.providePlayerInteractor(trackUrl)
    }

    init {
        playerInteractor.preparePlayer(
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

    companion object {
        fun getViewModelFactory(trackUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl)
            }
        }
    }

}