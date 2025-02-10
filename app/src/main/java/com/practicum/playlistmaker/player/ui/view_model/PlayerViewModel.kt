package com.practicum.playlistmaker.player.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.LibraryInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.PlayerUiState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private var track: Track,
    private val playerInteractor: PlayerInteractor,
    private val libraryInteractor: LibraryInteractor
): ViewModel() {

    private var timerJob: Job? = null

    private val stateLiveData = MutableLiveData<PlayerUiState>()
    fun observeState(): LiveData<PlayerUiState> = stateLiveData

    init {
        initPlayer()
    }

    private fun initPlayer() {
        playerInteractor.preparePlayer(
            track.previewUrl,
            onPrepared = object : PlayerInteractor.OnPreparedListener {
                override fun onPrepared() {
                    updateState(PlayerUiState.Prepared(isFavorite()))
                }
            },
            onCompleted = object : PlayerInteractor.OnCompletedListener {
                override fun onComplete() {
                    timerJob?.cancel()
                    updateState(PlayerUiState.Prepared(isFavorite()))
                }
            }
        )
    }

    fun onPlayButtonClicked() {
        when(stateLiveData.value) {
            is PlayerUiState.Playing -> {
                pausePlayer()
            }
            is PlayerUiState.Prepared,
            is PlayerUiState.Paused,
            is PlayerUiState.Default -> {
                startPlayer()
            }
            else -> { }
        }
    }

    fun onPause() {
        if (playerInteractor.getStatePlaying()) pausePlayer()
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        updateState(PlayerUiState.Playing(getCurrentPlayerPosition(), isFavorite()))
        startTimer()
    }

    private fun pausePlayer() {
            playerInteractor.pausePlayer()
            timerJob?.cancel()
            updateState(PlayerUiState.Paused(getCurrentPlayerPosition(), isFavorite()))
    }

    private fun releasePlayer() {
        playerInteractor.releasePlayer()
        stateLiveData.value = PlayerUiState.Default(isFavorite())
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerInteractor.getStatePlaying()) {
                delay(CHECK_INTERVAL)
                updateState(PlayerUiState.Playing(getCurrentPlayerPosition(), isFavorite()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentPosition()
            ) ?: "00:00"
    }

    private fun updateState(state: PlayerUiState) {
        stateLiveData.postValue(state)
    }

    private fun isFavorite(): Boolean {
        return track.isFavorite
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (isFavorite()) {
                libraryInteractor.deleteFromFavoriteTracks(track)
                track = track.copy(isFavorite = !isFavorite())
            } else {
                libraryInteractor.addToFavoriteTracks(track)
                track = track.copy(isFavorite = !isFavorite())
            }

            stateLiveData.value?.let { currentState ->
                currentState.isFavorite = isFavorite()
                updateState(currentState)
            }

        }
    }


    companion object {
        private const val CHECK_INTERVAL = 300L
    }

}