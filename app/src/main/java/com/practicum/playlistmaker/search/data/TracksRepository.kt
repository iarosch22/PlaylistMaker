package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.util.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>

    fun getSearchedTracks() : ArrayList<Track>

    fun saveSearchedTracks(tracks: ArrayList<Track>)

    fun addTrackToHistory(track: Track)

    fun clearHistory()
}