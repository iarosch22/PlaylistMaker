package com.practicum.playlistmaker.aboutplaylist.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.aboutplaylist.ui.AboutPlaylistUiState
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AboutPlaylistViewModel(
    private val playlistId: Long,
    private val interactor: CreationPlaylistInteractor,
    private val navigator: ExternalNavigator
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

    fun sharePlaylist() {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)
            val tracks = interactor.getTracks(playlist.tracksId)

            if (tracks.isEmpty()) {
                updateState(AboutPlaylistUiState.ShareContent(true, playlist, tracks))
            } else {
                updateState(AboutPlaylistUiState.ShareContent(false, playlist, tracks))
            }
        }
    }

    private fun updateState(state: AboutPlaylistUiState) {
        aboutPlaylistLiveData.postValue(state)
    }

}