package com.practicum.playlistmaker.newplaylist.data

import com.google.gson.Gson
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.newplaylist.data.converters.NewPlaylistDbConvertor
import com.practicum.playlistmaker.newplaylist.domain.db.NewPlaylistRepository
import com.practicum.playlistmaker.newplaylist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewPlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val converter: NewPlaylistDbConvertor
): NewPlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistsDao().getPlaylists().map { playlists ->
            converter.map(playlists)
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistsDao().updatePlaylist(
            converter.map(playlist)
        )
    }

    override suspend fun savePlaylist(playlist: Playlist) {
        appDatabase.playlistsDao().insertPlaylist(
            converter.map(playlist)
        )
    }

}