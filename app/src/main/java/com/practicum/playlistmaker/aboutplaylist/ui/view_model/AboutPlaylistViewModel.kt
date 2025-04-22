package com.practicum.playlistmaker.aboutplaylist.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.aboutplaylist.ui.AboutPlaylistUiState
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AboutPlaylistViewModel(
    private val playlistId: Long,
    private val interactor: CreationPlaylistInteractor
): ViewModel() {

    private val aboutPlaylistLiveData = MutableLiveData<AboutPlaylistUiState>()
    fun observeAboutPlaylist(): LiveData<AboutPlaylistUiState> = aboutPlaylistLiveData

    init {
        initPlaylistInfo()
    }

    private fun initPlaylistInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = interactor.getPlaylistById(playlistId)
            val tracks = interactor.getTracks(playlist.tracksId)

            updateState(AboutPlaylistUiState.Content(playlist,tracks))
        }

    }

    fun deleteTrack(track: Track) {
        Log.d("AboutPlaylistViewModel", "Track deleted func")

        viewModelScope.launch(Dispatchers.IO) {
            val playlist = interactor.getPlaylistById(playlistId)

            interactor.deleteTrackFromPlaylist(playlist, track)
            Log.d("AboutPlaylistViewModel", "Track deleted")

            val updatedPlaylist = interactor.getPlaylistById(playlistId)
            val updatedTracks = interactor.getTracks(updatedPlaylist.tracksId)

            updateState(AboutPlaylistUiState.Content(updatedPlaylist, updatedTracks))
        }
    }

    private fun updateState(state: AboutPlaylistUiState) {
        Log.d("AboutPlaylistViewModel", "updateState: $state tracks")
        aboutPlaylistLiveData.postValue(state)
    }

}