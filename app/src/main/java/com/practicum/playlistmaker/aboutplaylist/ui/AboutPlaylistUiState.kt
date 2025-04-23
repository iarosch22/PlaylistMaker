package com.practicum.playlistmaker.aboutplaylist.ui

import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

sealed class AboutPlaylistUiState {

    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>
    ): AboutPlaylistUiState()

    data class ShareContent(
        val isEmptyContent: Boolean,
        val playlist: Playlist,
        val tracks: List<Track>
    ): AboutPlaylistUiState()

}