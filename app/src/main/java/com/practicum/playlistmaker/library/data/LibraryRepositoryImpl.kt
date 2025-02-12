package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.library.data.converters.TrackDbConvertor
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.library.domain.db.LibraryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val convertor: TrackDbConvertor
): LibraryRepository {

    override fun favoriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracks()
            .map { tracks ->  convertFromTracksEntities(tracks)}
    }

    override suspend fun getTrackFavoriteValue(trackId: String): Boolean {
        val trackIds = getTracksId()
        return trackId in trackIds
    }

    override suspend fun getTracksId(): List<String> {
        return appDatabase.trackDao().getTracksId()
    }

    override suspend fun addToFavoriteTracks(track: Track) {
        appDatabase.trackDao().insertTrack(convertor.map(track))
    }

    override suspend fun deleteFromFavoriteTracks(track: Track) {
        appDatabase.trackDao().deleteTrack(convertor.map(track))
    }

    private suspend fun convertFromTracksEntities(tracks: List<TrackEntity>): List<Track> {
        val trackIds = getTracksId()

        return tracks.map { Track(
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
                isFavorite = it.trackId in trackIds
            )
        }
    }

}