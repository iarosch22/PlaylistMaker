package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TracksState
import com.practicum.playlistmaker.util.ErrorMessageType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TracksSearchViewModel(private val tracksInteractor: TracksInteractor): ViewModel() {

    private var searchJob: Job? = null

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private var latestSearchText: String? = null

    init {
        loadSearchedTracks()
    }

    private fun loadSearchedTracks() {
        stateLiveData.postValue(TracksState.HistoryContent(tracksInteractor.getSearchedTracks()))
    }

    fun saveSearchedTracks(tracks: ArrayList<Track>) {
        tracksInteractor.saveSearchedTracks(tracks)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        latestSearchText = changedText

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
            performSearch(changedText)
        }
    }

    private fun performSearch(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                renderState(
                    TracksState.Error(errorMessage = ErrorMessageType.SOMETHING_WENT_WRONG)
                )
            }
            tracks.isEmpty() -> {
                renderState(
                    TracksState.Error(errorMessage = ErrorMessageType.NOTHING_FOUND)
                )
            }
            else -> {
                renderState(
                    TracksState.SearchedContent(searchedTracks = tracks)
                )
            }
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }

}