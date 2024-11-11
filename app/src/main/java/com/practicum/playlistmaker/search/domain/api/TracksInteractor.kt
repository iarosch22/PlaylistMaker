package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksInteractor {

    fun searchTracks(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }

    fun getSearchedTracks(): ArrayList<Track>

    fun saveSearchedTracks(tracks: ArrayList<Track>)

    fun addTrackToHistory(track: Track)

    fun clearHistory()
}