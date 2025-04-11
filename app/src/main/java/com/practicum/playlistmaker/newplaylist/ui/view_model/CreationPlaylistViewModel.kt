package com.practicum.playlistmaker.newplaylist.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.newplaylist.domain.db.CreationPlaylistInteractor
import com.practicum.playlistmaker.newplaylist.domain.models.Playlist
import com.practicum.playlistmaker.newplaylist.ui.PlaylistUiState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreationPlaylistViewModel(
    private val playlistId: Long,
    private val interactor: CreationPlaylistInteractor
): ViewModel() {

    private var pathToSelectedPhoto: Uri? = null
    private var enteredName = ""
    private var enteredDescription = ""
    private val tracks: List<Track> = mutableListOf()

    private val stateLiveData = MutableLiveData<PlaylistUiState>()
    fun observeState(): LiveData<PlaylistUiState> = stateLiveData

    init {
        initState()
    }

    private fun initState() {
        if (playlistId == -1L) {
            updateState(PlaylistUiState.NewPlaylistMode)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val playlist = interactor.getPlaylistById(playlistId)
                updateState(PlaylistUiState.EditPlaylistMode(playlist))
            }
        }
    }

    fun setNameValue(name: String = "") {
        if (name.isEmpty()) {
            updateState(PlaylistUiState.SaveButtonEnabled(isEnabled = false))
            enteredName = ""
        } else {
            enteredName = name
            updateState(PlaylistUiState.SaveButtonEnabled(isEnabled = true))
        }
    }

    fun setDescriptionValue(description: String = "") {
        if (description.isEmpty()) {
            updateState(PlaylistUiState.SaveButtonEnabled(isEnabled = false))
            enteredDescription = ""
        } else {
            enteredDescription = description
            updateState(PlaylistUiState.SaveButtonEnabled(isEnabled = true))
        }
    }

    fun setPathToPhoto(uri: Uri) {
        pathToSelectedPhoto = uri
    }

    fun closeFragment() {
        if (
            pathToSelectedPhoto != null &&
            enteredName.isNotEmpty() ||
            enteredDescription.isNotEmpty()
            ) {
            updateState(PlaylistUiState.CloseWithConfirmation(true))
        } else {
            updateState(PlaylistUiState.CloseWithConfirmation(false))
        }
    }

    fun savePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.savePlaylist(
                Playlist(
                    id = 0L,
                    name = enteredName,
                    description = enteredDescription,
                    pathToCover = pathToSelectedPhoto.toString(),
                    tracksId = tracks.map { it.trackId },
                    size = tracks.size.toString()
                )
            )

            updateState(PlaylistUiState.SavingPlaylist)
        }
    }

    private fun updateState(state: PlaylistUiState) {
        stateLiveData.postValue(state)
    }
}