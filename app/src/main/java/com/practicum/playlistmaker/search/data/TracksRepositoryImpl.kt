package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.data.dto.preferences.SearchHistoryLocalDataSource
import com.practicum.playlistmaker.search.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistoryLocalDataSource: SearchHistoryLocalDataSource,
    private val appDatabase: AppDatabase
) :
    TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when(response.resultCode) {
            -1 ->  emit(Resource.Error("Проверьте подключение к интернету"))

            200 -> {
                val loadedData = Resource.Success((response as TrackSearchResponse)).data?.results

                val savedData = appDatabase.trackDao().getTracksId()

                val data = loadedData?.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl,
                        isFavorite = it.trackId in savedData,
                    )
                }

                emit(Resource.Success(data))
            }
            else -> emit(Resource.Error("Ошибка сервера"))
        }
    }

    override fun getSearchedTracks(): ArrayList<Track> {
        return searchHistoryLocalDataSource.readTracksFromSearchHistory()
    }

    override fun saveSearchedTracks(tracks: ArrayList<Track>) {
        searchHistoryLocalDataSource.saveToSearchHistory(tracks)
    }

    override fun addTrackToHistory(track: Track) {
        searchHistoryLocalDataSource.addTrackToHistory(track)
    }

    override fun clearHistory() {
        searchHistoryLocalDataSource.clearHistory()
    }

}