package com.practicum.playlistmaker.creationplaylist.data.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.creationplaylist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.player.data.db.PlaylistsTrackEntity
import com.practicum.playlistmaker.search.domain.models.Track

class NewPlaylistDbConvertor {

    private val gson = Gson()

    private val listType = object : TypeToken<List<String>>() {}.type

    fun map(playlist: Playlist): PlaylistEntity {
        return  PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            pathToCover = playlist.pathToCover,
            tracksId = gson.toJson(playlist.tracksId),
            size = playlist.size
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            pathToCover = playlist.pathToCover,
            tracksId = gson.fromJson(playlist.tracksId, listType),
            size = playlist.size
        )
    }

    fun map(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map {
            Playlist(
                id = it.id,
                name = it.name,
                description = it.description,
                pathToCover = it.pathToCover,
                tracksId = gson.fromJson(it.tracksId, listType),
                size = it.size
            )
        }
    }

    fun map(track: Track): PlaylistsTrackEntity {
        return PlaylistsTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            coverArtwork = track.getCoverArtwork(),
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun filterTracks(trackIds: List<String>, tracks: List<PlaylistsTrackEntity>): List<Track> {

        val tracksMap = tracks.associateBy { it.trackId }

        return trackIds.mapNotNull { trackId ->
            tracksMap[trackId]?.let {
                Track(
                    trackId = it.trackId,
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTimeMillis = it.trackTimeMillis,
                    artworkUrl100 = it.artworkUrl100,
                    collectionName = it.collectionName,
                    releaseDate = it.releaseDate,
                    primaryGenreName = it.primaryGenreName,
                    country = it.country,
                    previewUrl = it.previewUrl
                )
            }
        }
    }
}