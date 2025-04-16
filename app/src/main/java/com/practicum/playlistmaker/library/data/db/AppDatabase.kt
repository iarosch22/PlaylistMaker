package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.creationplaylist.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.library.data.db.dao.TrackDao
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import com.practicum.playlistmaker.creationplaylist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.player.data.db.PlaylistsTrackDao
import com.practicum.playlistmaker.player.data.db.PlaylistsTrackEntity

@Database(
    version = 3,
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistsTrackEntity::class],
    exportSchema = true
    )
abstract class AppDatabase: RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistsDao(): PlaylistsDao

    abstract fun playlistsTrackDao(): PlaylistsTrackDao

}