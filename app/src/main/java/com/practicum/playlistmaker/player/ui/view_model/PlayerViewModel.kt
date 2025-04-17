package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistInteractor
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.library.domain.db.LibraryInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.PlayerUiState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val libraryInteractor: LibraryInteractor,
    private val creationPlaylistInteractor: CreationPlaylistInteractor
) : ViewModel() {

    private var isFavorite = track.isFavorite
    private var timerJob: Job? = null

    private val stateLiveData = MutableLiveData<PlayerUiState>()
    fun observeState(): LiveData<PlayerUiState> = stateLiveData

    private val playlistsLiveData = MutableStateFlow<List<Playlist>>(emptyList())
    fun observePlaylistsState(): StateFlow<List<Playlist>> = playlistsLiveData

    init {
        setFavoriteValue()
        initPlayer()
        getPlaylists()
    }

    private fun initPlayer() {
        playerInteractor.preparePlayer(
            track.previewUrl,
            onPrepared = object : PlayerInteractor.OnPreparedListener {
                override fun onPrepared() {
                    updateState(PlayerUiState.Prepared)
                }
            },
            onCompleted = object : PlayerInteractor.OnCompletedListener {
                override fun onComplete() {
                    timerJob?.cancel()
                    updateState(PlayerUiState.Prepared)
                }
            }
        )
    }

    fun onPlayButtonClicked() {
        when (stateLiveData.value) {
            is PlayerUiState.Playing -> pausePlayer()
            is PlayerUiState.Prepared,
            is PlayerUiState.Paused,
            is PlayerUiState.Default -> startPlayer()
            else -> {}
        }
    }

    fun onPause() {
        if (playerInteractor.getStatePlaying()) pausePlayer()
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        updateState(PlayerUiState.Playing(getCurrentPlayerPosition(), isFavorite))
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        timerJob?.cancel()
        updateState(PlayerUiState.Paused(getCurrentPlayerPosition(), isFavorite))
    }

    private fun releasePlayer() {
        playerInteractor.releasePlayer()
        stateLiveData.value = PlayerUiState.Default
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
                updateState(PlayerUiState.Playing(getCurrentPlayerPosition(), isFavorite))
            }
        }
    }

    private fun setFavoriteValue() {
        viewModelScope.launch {
            isFavorite = libraryInteractor.getTrackFavoriteValue(track.trackId)
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(playerInteractor.getCurrentPosition()) ?: DEFAULT_TIMER
    }

    private fun updateState(state: PlayerUiState) {
        stateLiveData.postValue(state)
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (isFavorite) {
                libraryInteractor.deleteFromFavoriteTracks(track)
            } else {
                libraryInteractor.addToFavoriteTracks(track)
            }

            isFavorite = !isFavorite

            val currentState = stateLiveData.value
            val progress = when (currentState) {
                is PlayerUiState.Playing -> currentState.progress
                is PlayerUiState.Paused -> currentState.progress
                else -> DEFAULT_TIMER
            }

            when (currentState) {
                is PlayerUiState.Playing -> updateState(PlayerUiState.Playing(progress, isFavorite))
                is PlayerUiState.Paused -> updateState(PlayerUiState.Paused(progress, isFavorite))
                is PlayerUiState.Prepared -> updateState(PlayerUiState.Prepared)
                is PlayerUiState.Default -> updateState(PlayerUiState.Default)
                else -> {}
            }
        }
    }

    private fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            creationPlaylistInteractor.getPlaylists()
                .collect { playlists ->
                    playlistsLiveData.value = playlists
                }
        }
    }

    fun hasTrackInPlaylist(playlist: Playlist, tracksId: List<String>) {
        if (tracksId.contains(track.trackId)) {
            updateState(PlayerUiState.AddingToPlaylist(playlist.name, false))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                creationPlaylistInteractor.addTrackToPlaylist(playlist, track)
                updateState(PlayerUiState.AddingToPlaylist(playlist.name, true))
            }
        }
    }

    companion object {
        private const val CHECK_INTERVAL = 300L

        private const val DEFAULT_TIMER = "00:00"
    }
}
