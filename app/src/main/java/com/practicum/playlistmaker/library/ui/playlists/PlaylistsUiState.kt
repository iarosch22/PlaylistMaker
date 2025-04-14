package com.practicum.playlistmaker.library.ui.playlists

import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist

sealed class PlaylistsUiState {

    data class Content(val playlists: List<Playlist>): PlaylistsUiState()

    object Empty: PlaylistsUiState()

}