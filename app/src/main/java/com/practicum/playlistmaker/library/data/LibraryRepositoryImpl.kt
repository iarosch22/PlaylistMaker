package com.practicum.playlistmaker.library.data

import com.practicum.playlistmaker.library.data.converters.TrackDbConvertor
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.library.domain.db.LibraryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LibraryRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val convertor: TrackDbConvertor
): LibraryRepository {

    override fun favoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTracksEntities(tracks))
    }

    override suspend fun addToFavoriteTracks(track: Track) {
        appDatabase.trackDao().insertTrack(convertor.map(track))
    }

    override suspend fun deleteFromFavoriteTracks(track: Track) {
        appDatabase.trackDao().deleteTrack(convertor.map(track))
    }

    private fun convertFromTracksEntities(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> convertor.map(track) }
    }

}