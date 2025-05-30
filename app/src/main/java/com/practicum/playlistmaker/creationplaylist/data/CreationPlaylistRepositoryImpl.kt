package com.practicum.playlistmaker.creationplaylist.data

import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.creationplaylist.data.converters.NewPlaylistDbConvertor
import com.practicum.playlistmaker.creationplaylist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.creationplaylist.domain.db.CreationPlaylistRepository
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.player.data.db.PlaylistsTrackEntity
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
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

    override suspend fun deletePlaylist(playlist: Playlist) {
        val tracks = getTracks(playlist.tracksId)

        appDatabase.playlistsDao().deletePlaylist(converter.map(playlist))

        tracks.forEach { track ->
            deleteTrackFromDb(converter.map(track))
        }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val tracksId = track.trackId

        val newTracksId = playlist.tracksId.toMutableList().apply {
            add(0, tracksId)
        }

        val updatedPlaylist = playlist.copy(
            tracksId = newTracksId,
            size = (playlist.size.toInt() + 1).toString()
        )

        updatePlaylist(updatedPlaylist)

        appDatabase.playlistsTrackDao().insertTrack(
            converter.map(track)
        )
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track) {
        val tracksId = track.trackId

        val newTracksId = playlist.tracksId.toMutableList().apply {
            remove(tracksId)
        }

        val updatedPlaylist = playlist.copy(
            tracksId = newTracksId,
            size = (playlist.size.toInt() - 1).toString()
        )

        updatePlaylist(updatedPlaylist)
        deleteTrackFromDb(converter.map(track))
    }

    private suspend fun deleteTrackFromDb(track: PlaylistsTrackEntity) {
        var trackIsUsed = false
        appDatabase.playlistsDao().getPlaylists().map { playlist ->
            playlist.map {
                if (it.tracksId.contains(track.trackId)) {
                    trackIsUsed = true
                }
            }
        }
        if (trackIsUsed) {
            appDatabase.playlistsTrackDao().deleteTrack(track)
        }
    }

    override suspend fun getTracks(trackIds: List<String>): List<Track>  {
        val tracks = appDatabase.playlistsTrackDao().getTracks().toMutableList()
        return converter.filterTracks(trackIds, tracks)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistsDao().updatePlaylist( converter.map(playlist) )
    }

}