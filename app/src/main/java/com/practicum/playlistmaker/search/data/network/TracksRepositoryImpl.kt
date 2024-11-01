package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.data.dto.preferences.TrackManager
import com.practicum.playlistmaker.search.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient, private val trackManager: TrackManager) :
    TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        return when(response.resultCode) {
            -1 -> { Resource.Error("Проверьте подключение к интернету") }

            200 -> {
                Resource.Success((response as TrackSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })
            }

            else -> { Resource.Error("Ошибка сервера") }
        }
    }

    override fun getSearchedTracks(): ArrayList<Track> {
        return trackManager.readTracksFromSearchHistory()
    }

    override fun saveSearchedTracks(tracks: ArrayList<Track>) {
        trackManager.saveToSearchHistory(tracks)
    }

    override fun addTrackToHistory(track: Track) {
        trackManager.addTrackToHistory(track)
    }

    override fun clearHistory() {
        trackManager.clearHistory()
    }

}