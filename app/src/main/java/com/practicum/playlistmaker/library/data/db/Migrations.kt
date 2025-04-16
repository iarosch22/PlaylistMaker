package com.practicum.playlistmaker.library.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_TO_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `playlists_table` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `pathToCover` TEXT NOT NULL,
                `tracksId` TEXT NOT NULL,
                `size` TEXT NOT NULL
            )
        """.trimIndent())
    }
}

val MIGRATION_2_TO_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `playlist_tracks_table` (
            trackId TEXT NOT NULL PRIMARY KEY,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                trackTimeMillis TEXT NOT NULL,
                artworkUrl100 TEXT NOT NULL,
                coverArtwork TEXT NOT NULL,
                collectionName TEXT NOT NULL,
                releaseDate TEXT NOT NULL,
                primaryGenreName TEXT NOT NULL,
                country TEXT NOT NULL,
                previewUrl TEXT NOT NULL
            )
        """.trimIndent())
    }
}