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