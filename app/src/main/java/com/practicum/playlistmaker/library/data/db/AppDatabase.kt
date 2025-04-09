package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.newplaylist.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.library.data.db.dao.TrackDao
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase: RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistsDao(): PlaylistsDao

}