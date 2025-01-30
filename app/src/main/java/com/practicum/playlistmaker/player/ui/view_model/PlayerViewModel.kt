package com.practicum.playlistmaker.player.ui.view_model

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
    private val trackUrl: String,
    private val playerInteractor: PlayerInteractor,
    private val libraryInteractor: LibraryInteractor
): ViewModel() {

    private var timerJob: Job? = null

    private val stateLiveData = MutableLiveData<PlayerUiState>(PlayerUiState.Default())
    fun observeState(): LiveData<PlayerUiState> = stateLiveData

    private val favoriteLiveData = MutableLiveData<Boolean>(false)
    fun observeFavorite(): LiveData<Boolean> = favoriteLiveData

    init {
        initPlayer()
    }

    private fun initPlayer() {
        playerInteractor.preparePlayer(
            trackUrl,
            onPrepared = object : PlayerInteractor.OnPreparedListener {
                override fun onPrepared() {
                    updateState(PlayerUiState.Prepared())
                }
            },
            onCompleted = object : PlayerInteractor.OnCompletedListener {
                override fun onComplete() {
                    timerJob?.cancel()
                    updateState(PlayerUiState.Prepared())
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
        updateState(PlayerUiState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
            playerInteractor.pausePlayer()
            timerJob?.cancel()
            updateState(PlayerUiState.Paused(getCurrentPlayerPosition()))
    }

    private fun releasePlayer() {
        playerInteractor.releasePlayer()
        stateLiveData.value = PlayerUiState.Default()
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
                updateState(PlayerUiState.Playing(getCurrentPlayerPosition()))
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

    private fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (favoriteLiveData.value == true) {
                favoriteLiveData.postValue(false)
                libraryInteractor.addToFavoriteTracks(track)
            } else {
                favoriteLiveData.postValue(true)
                libraryInteractor.deleteFromFavoriteTracks(track)
            }
        }
    }


    companion object {
        private const val CHECK_INTERVAL = 300L
    }

}