package com.practicum.playlistmaker.newplaylist.domain.impl

import com.practicum.playlistmaker.newplaylist.domain.db.CreationPlaylistInteractor
import com.practicum.playlistmaker.newplaylist.domain.db.CreationPlaylistRepository
import com.practicum.playlistmaker.newplaylist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class CreationPlaylistInteractorImpl(private val repository: CreationPlaylistRepository): CreationPlaylistInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist {
        return repository.getPlaylistById(playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun savePlaylist(playlist: Playlist) {
        repository.savePlaylist(playlist)
    }

}