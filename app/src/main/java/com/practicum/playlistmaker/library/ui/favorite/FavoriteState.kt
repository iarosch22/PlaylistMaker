package com.practicum.playlistmaker.library.ui.favorite

import com.practicum.playlistmaker.search.domain.models.Track

sealed class FavoriteState {

    data class Content(val favoriteTracks: List<Track>): FavoriteState()

    data object Empty : FavoriteState()

}