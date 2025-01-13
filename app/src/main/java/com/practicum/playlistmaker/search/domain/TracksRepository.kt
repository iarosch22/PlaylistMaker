package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.util.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>

    fun getSearchedTracks() : ArrayList<Track>

    fun saveSearchedTracks(tracks: ArrayList<Track>)

    fun addTrackToHistory(track: Track)

    fun clearHistory()
}