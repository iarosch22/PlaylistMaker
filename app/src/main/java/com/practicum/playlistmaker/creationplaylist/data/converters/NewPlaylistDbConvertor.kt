package com.practicum.playlistmaker.creationplaylist.data.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.creationplaylist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist

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
}