package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.data.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.util.Resource

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