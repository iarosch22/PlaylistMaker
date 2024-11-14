package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.data.dto.preferences.LocalDataSource
import com.practicum.playlistmaker.search.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient, private val localDataSource: LocalDataSource) :
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
        return localDataSource.readTracksFromSearchHistory()
    }

    override fun saveSearchedTracks(tracks: ArrayList<Track>) {
        localDataSource.saveToSearchHistory(tracks)
    }

    override fun addTrackToHistory(track: Track) {
        localDataSource.addTrackToHistory(track)
    }

    override fun clearHistory() {
        localDataSource.clearHistory()
    }

}