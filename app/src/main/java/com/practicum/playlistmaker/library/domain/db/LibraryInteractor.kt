package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface LibraryInteractor {

    fun favoriteTracks(): Flow<List<Track>>

    suspend fun getTracksId(): List<String>

    suspend fun addToFavoriteTracks(track: Track)

    suspend fun deleteFromFavoriteTracks(track: Track)

}