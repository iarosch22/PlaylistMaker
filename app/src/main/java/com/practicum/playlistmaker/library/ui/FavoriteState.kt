package com.practicum.playlistmaker.library.ui

import com.practicum.playlistmaker.search.domain.models.Track

sealed class FavoriteState {

    data class Content(val favoriteTracks: List<Track>): FavoriteState()

    data object Empty : FavoriteState()

}