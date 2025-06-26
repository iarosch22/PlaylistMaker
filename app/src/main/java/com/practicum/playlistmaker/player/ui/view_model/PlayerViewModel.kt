package com.practicum.playlistmaker.player.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistInteractor
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.library.domain.db.LibraryInteractor
import com.practicum.playlistmaker.player.ui.AudioPlayerControl
import com.practicum.playlistmaker.player.ui.PlayerUiState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val libraryInteractor: LibraryInteractor,
    private val creationPlaylistInteractor: CreationPlaylistInteractor
) : ViewModel() {

    private var isFavorite = track.isFavorite

    private val playerState = MutableLiveData<PlayerUiState>()
    fun observePlayerState(): LiveData<PlayerUiState> = playerState

    private val favoriteLiveData = MutableLiveData<Boolean>()
    fun observeFavorite(): LiveData<Boolean> = favoriteLiveData

    private val playlistsState = MutableStateFlow<List<Playlist>>(emptyList())
    fun observePlaylistsState(): StateFlow<List<Playlist>> = playlistsState

    private var audioPlayerControl: AudioPlayerControl? = null

    init {
        setFavoriteValue()
        getPlaylists()
    }

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl

        viewModelScope.launch {
            audioPlayerControl.getPlayerState().collect {
                playerState.postValue(it)
            }
        }
    }

    fun startNotification() {
        when(playerState.value) {
            is PlayerUiState.Playing -> audioPlayerControl?.startNotification()
            else -> {}
        }
    }

    fun stopNotification() {
        audioPlayerControl?.stopNotification()
    }

    fun onPlayButtonClicked() {
        when (playerState.value) {
            is PlayerUiState.Playing -> audioPlayerControl?.pausePlayer()
            else -> audioPlayerControl?.startPlayer()
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerControl = null
    }

    private fun setFavoriteValue() {
        viewModelScope.launch {
            isFavorite = libraryInteractor.getTrackFavoriteValue(track.trackId)
            favoriteLiveData.value = isFavorite
        }
    }

    private fun updateState(state: PlayerUiState) {
        playerState.postValue(state)
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (isFavorite) {
                libraryInteractor.deleteFromFavoriteTracks(track)
            } else {
                libraryInteractor.addToFavoriteTracks(track)
            }

            favoriteLiveData.value = !isFavorite
            isFavorite = !isFavorite
        }
    }

    private fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            creationPlaylistInteractor.getPlaylists()
                .collect { playlists ->
                    playlistsState.value = playlists
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
}
