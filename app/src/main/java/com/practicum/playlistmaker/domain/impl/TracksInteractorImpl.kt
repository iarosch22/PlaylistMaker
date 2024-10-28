package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.util.Resource

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TrackConsumer) {
        val t = Thread {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Error -> { consumer.consume(null, resource.message)}
                is Resource.Success -> { consumer.consume(resource.data, null) }
            }
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