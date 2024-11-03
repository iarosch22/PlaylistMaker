package com.practicum.playlistmaker.search.ui.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface TracksHistoryState {
    object Loading: TracksHistoryState

    data class Content(val tracks: List<Track>) : TracksHistoryState

    data class Error(val errorMessage: String): TracksHistoryState
}