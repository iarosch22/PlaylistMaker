package com.practicum.playlistmaker.newplaylist.domain.impl

import com.practicum.playlistmaker.newplaylist.domain.db.NewPlaylistInteractor
import com.practicum.playlistmaker.newplaylist.domain.db.NewPlaylistRepository
import com.practicum.playlistmaker.newplaylist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class NewPlaylistInteractorImpl(private val repository: NewPlaylistRepository): NewPlaylistInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun savePlaylist(playlist: Playlist) {
        repository.savePlaylist(playlist)
    }

}