package com.practicum.playlistmaker.creationplaylist.ui.view_model

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistInteractor
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.creationplaylist.ui.CreationPlaylistUiState
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

    private val stateLiveData = MutableLiveData<CreationPlaylistUiState>()
    fun observeState(): LiveData<CreationPlaylistUiState> = stateLiveData

    init {
        initState()
    }

    private fun initState() {
        if (playlistId == NEW_PLAYLIST_MODE) {
            updateState(CreationPlaylistUiState.NewCreationPlaylistMode)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val playlist = interactor.getPlaylistById(playlistId)
                pathToSelectedPhoto = playlist.pathToCover.toUri()
                enteredName = playlist.name
                enteredDescription = playlist.description
                updateState(CreationPlaylistUiState.EditCreationPlaylistMode(playlist))
            }
        }
    }

    fun setNameValue(name: String = "") {
        if (name.isEmpty()) {
            updateState(CreationPlaylistUiState.SaveButtonEnabled(isEnabled = false))
            enteredName = ""
        } else {
            enteredName = name
            updateState(CreationPlaylistUiState.SaveButtonEnabled(isEnabled = true))
        }
    }

    fun setDescriptionValue(description: String = "") {
        if (description.isEmpty()) {
            updateState(CreationPlaylistUiState.SaveButtonEnabled(isEnabled = false))
            enteredDescription = ""
        } else {
            enteredDescription = description
            updateState(CreationPlaylistUiState.SaveButtonEnabled(isEnabled = true))
        }
    }

    fun setPathToPhoto(uri: Uri) {
        pathToSelectedPhoto = uri
    }

    fun closeFragment() {
        if (playlistId != NEW_PLAYLIST_MODE) {
            updateState(CreationPlaylistUiState.CloseWithConfirmation(false))
        } else if (
            pathToSelectedPhoto != null &&
            enteredName.isNotEmpty() ||
            enteredDescription.isNotEmpty()
            ) {
            updateState(CreationPlaylistUiState.CloseWithConfirmation(true))
        } else {
            updateState(CreationPlaylistUiState.CloseWithConfirmation(false))
        }
    }

    fun savePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            if (playlistId == NEW_PLAYLIST_MODE) {
                interactor.savePlaylist(
                    Playlist(
                        id = 0L,
                        name = enteredName,
                        description = enteredDescription,
                        pathToCover = pathToSelectedPhoto.toString(),
                        tracksId = emptyList(),
                        size = DEFAULT_SIZE
                    )
                )

                updateState(CreationPlaylistUiState.SavingCreationPlaylist(enteredName, true))
            } else {
                val playlist = interactor.getPlaylistById(playlistId)

                val updatedPlaylist = playlist.copy(
                    pathToCover = pathToSelectedPhoto.toString(),
                    name = enteredName,
                    description = enteredDescription
                )

                interactor.updatePlaylist(updatedPlaylist)
                updateState(CreationPlaylistUiState.SavingCreationPlaylist(enteredName, false))
            }
        }
    }

    private fun updateState(state: CreationPlaylistUiState) {
        stateLiveData.postValue(state)
    }

    companion object {
        const val NEW_PLAYLIST_MODE = -1L
        const val DEFAULT_SIZE = "0"
    }
}