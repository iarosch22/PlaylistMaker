package com.practicum.playlistmaker.aboutplaylist.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.aboutplaylist.ui.AboutPlaylistUiState
import com.practicum.playlistmaker.aboutplaylist.ui.EditModeState
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AboutPlaylistViewModel(
    private val playlistId: Long,
    private val interactor: CreationPlaylistInteractor
): ViewModel() {

    private val aboutPlaylistLiveData = MutableLiveData<AboutPlaylistUiState>()
    fun observeAboutPlaylist(): LiveData<AboutPlaylistUiState> = aboutPlaylistLiveData

    private val editMode = SingleLiveEvent<EditModeState>()
    fun observeEditMode(): LiveData<EditModeState> = editMode

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

    fun editModeStarted() {
        editMode.postValue(EditModeState.None)
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = interactor.getPlaylistById(playlistId)

            interactor.deleteTrackFromPlaylist(playlist, track)
            Log.d("AboutPlaylistViewModel", "Track deleted")

            val updatedPlaylist = interactor.getPlaylistById(playlistId)
            val updatedTracks = interactor.getTracks(updatedPlaylist.tracksId)

            updateState(AboutPlaylistUiState.Content(updatedPlaylist, updatedTracks))
        }
    }

    fun showDeletePlaylistDialog() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = interactor.getPlaylistById(playlistId)
            updateState(AboutPlaylistUiState.ShowDeletePlaylistDialog(playlist.name))
        }
    }

    fun startEditingPlaylist() {
        editMode.postValue(EditModeState.Show(playlistId))
    }

    fun deletePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = interactor.getPlaylistById(playlistId)
            interactor.deletePlaylist(playlist)
        }
    }

    fun sharePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
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