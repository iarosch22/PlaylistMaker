package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.db.LibraryInteractor
import com.practicum.playlistmaker.library.domain.db.LibraryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryInteractorImpl(private val repository: LibraryRepository): LibraryInteractor {

    override fun favoriteTracks(): Flow<List<Track>> {
        return repository.favoriteTracks().map { tracks ->
            tracks.reversed()
        }
    }

    override suspend fun addToFavoriteTracks(track: Track) {
        return repository.addToFavoriteTracks(track)
    }

    override suspend fun deleteFromFavoriteTracks(track: Track) {
        return repository.deleteFromFavoriteTracks(track)
    }

}