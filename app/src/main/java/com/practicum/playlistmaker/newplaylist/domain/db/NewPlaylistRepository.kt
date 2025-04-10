package com.practicum.playlistmaker.newplaylist.domain.db

import com.practicum.playlistmaker.newplaylist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface NewPlaylistRepository {

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun savePlaylist(playlist: Playlist)

}