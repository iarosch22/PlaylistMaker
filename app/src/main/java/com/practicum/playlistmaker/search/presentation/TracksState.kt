package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.ErrorMessageType

sealed interface TracksState {
    object Loading: TracksState

    data class SearchedContent(val searchedTracks: List<Track>) : TracksState

    data class Error(val errorMessage: ErrorMessageType): TracksState

    data class HistoryContent(val savedTracks: List<Track>) : TracksState
}