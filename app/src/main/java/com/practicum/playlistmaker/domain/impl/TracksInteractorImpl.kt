package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TrackConsumer) {
        val t = Thread {
            consumer.consume(repository.searchTracks(expression))
        }

        t.start()
    }

    override fun getSearchedTracks(): ArrayList<Track> {
        return repository.getSearchedTracks()
    }

    override fun saveSearchedTracks(tracks: ArrayList<Track>) {
        repository.saveSearchedTracks(tracks)
    }

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

}