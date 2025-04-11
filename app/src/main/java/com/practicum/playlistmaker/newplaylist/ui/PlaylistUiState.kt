package com.practicum.playlistmaker.newplaylist.ui

import com.practicum.playlistmaker.newplaylist.domain.models.Playlist

sealed class PlaylistUiState {

    object NewPlaylistMode: PlaylistUiState()

    data class SavingPlaylist(val name: String): PlaylistUiState()

    data class SaveButtonEnabled(val isEnabled: Boolean): PlaylistUiState()

    data class EditPlaylistMode(val playlist: Playlist): PlaylistUiState()

    data class CloseWithConfirmation(val isShowDialog: Boolean): PlaylistUiState()

}