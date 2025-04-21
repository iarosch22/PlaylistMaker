package com.practicum.playlistmaker.player.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity

@Dao
interface PlaylistsTrackDao {

    @Insert(entity = PlaylistsTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistsTrackEntity)

    @Query("SELECT * FROM playlist_tracks_table")
    suspend fun getTracks(): List<TrackEntity>

}