package com.practicum.playlistmaker.creationplaylist.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String,
    val pathToCover: String,
    val tracksId: String,
    val size: String
)
