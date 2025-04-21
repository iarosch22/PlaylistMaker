package com.practicum.playlistmaker.library.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistInteractor
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.library.ui.playlists.PlaylistsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val interactor: CreationPlaylistInteractor): ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsUiState>()
    fun observeState(): LiveData<PlaylistsUiState> = stateLiveData

    init {
        getPlaylists()
    }


    private fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            interactor
                .getPlaylists()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(playlistsFromDb: List<Playlist>?) {
        val playlists = mutableListOf<Playlist>()

        if (playlistsFromDb != null) {
            playlists.addAll(playlistsFromDb)
        }

        when {
            playlists.isEmpty() -> updateState(PlaylistsUiState.Empty)
            else -> updateState(PlaylistsUiState.Content(playlists = playlists))
        }
    }

    private fun updateState(state: PlaylistsUiState) {
        stateLiveData.postValue(state)
    }

}