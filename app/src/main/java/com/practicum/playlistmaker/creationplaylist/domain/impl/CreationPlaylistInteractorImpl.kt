package com.practicum.playlistmaker.creationplaylist.domain.impl

import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistInteractor
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistRepository
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class CreationPlaylistInteractorImpl(private val repository: CreationPlaylistRepository): CreationPlaylistInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist {
        return repository.getPlaylistById(playlistId)
    }

    override suspend fun savePlaylist(playlist: Playlist) {
        repository.savePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        repository.addTrackToPlaylist(playlist, track)
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track) {
        repository.deleteTrackFromPlaylist(playlist, track)
    }

    override suspend fun getTracks(trackIds: List<String>): List<Track> {
        return repository.getTracks(trackIds)
    }

}