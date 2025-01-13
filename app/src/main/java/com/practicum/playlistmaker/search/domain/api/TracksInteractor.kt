package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>

    fun getSearchedTracks(): ArrayList<Track>

    fun saveSearchedTracks(tracks: ArrayList<Track>)

    fun addTrackToHistory(track: Track)

    fun clearHistory()
}