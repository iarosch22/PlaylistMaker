package com.practicum.playlistmaker.creationplaylist.domain.db

import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface CreationPlaylistInteractor {

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylistById(playlistId: Long): Playlist

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun savePlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(track: Track)

}