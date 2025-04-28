package com.practicum.playlistmaker.creationplaylist.ui

import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist

sealed class CreationPlaylistUiState {

    object NewCreationPlaylistMode: CreationPlaylistUiState()

    data class SavingCreationPlaylist(val name: String, val isNewPlaylist: Boolean): CreationPlaylistUiState()

    data class SaveButtonEnabled(val isEnabled: Boolean): CreationPlaylistUiState()

    data class EditCreationPlaylistMode(val playlist: Playlist): CreationPlaylistUiState()

    data class CloseWithConfirmation(val isShowDialog: Boolean): CreationPlaylistUiState()

}