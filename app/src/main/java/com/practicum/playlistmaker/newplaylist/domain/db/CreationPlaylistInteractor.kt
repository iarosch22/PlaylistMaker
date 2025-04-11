package com.practicum.playlistmaker.newplaylist.domain.db

import com.practicum.playlistmaker.newplaylist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.newplaylist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface CreationPlaylistInteractor {

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylistById(playlistId: Long): Playlist

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun savePlaylist(playlist: Playlist)


}