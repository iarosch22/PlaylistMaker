package com.practicum.playlistmaker.creationplaylist.data

import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.creationplaylist.data.converters.NewPlaylistDbConvertor
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistRepository
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CreationPlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val converter: NewPlaylistDbConvertor
): CreationPlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistsDao().getPlaylists().map { playlists ->
            converter.map(playlists)
        }
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist {
        return converter.map(appDatabase.playlistsDao().getPlaylistById(playlistId))
    }

    override suspend fun savePlaylist(playlist: Playlist) {
        appDatabase.playlistsDao().insertPlaylist(
            converter.map(playlist)
        )
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val tracksId = track.trackId

        val newTracksId = playlist.tracksId.toMutableList().apply {
            add(tracksId)
        }

        val updatedPlaylist = playlist.copy(
            tracksId = newTracksId,
            size = (playlist.size.toInt() + 1).toString()
        )

        appDatabase.playlistsDao().updatePlaylist( converter.map(updatedPlaylist))

        appDatabase.playlistsTrackDao().insertTrack(
            converter.map(track)
        )
    }

    override suspend fun getTracks(trackIds: List<String>): List<Track>  {
        val tracks = appDatabase.playlistsTrackDao().getTracks()
        return converter.filterTracks(trackIds, tracks)
    }

}