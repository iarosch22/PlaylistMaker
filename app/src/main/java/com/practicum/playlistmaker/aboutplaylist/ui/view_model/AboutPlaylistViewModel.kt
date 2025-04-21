package com.practicum.playlistmaker.aboutplaylist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.aboutplaylist.ui.AboutPlaylistUiState
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistInteractor
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

    private fun updateState(state: AboutPlaylistUiState) {
        aboutPlaylistLiveData.postValue(state)
    }

}