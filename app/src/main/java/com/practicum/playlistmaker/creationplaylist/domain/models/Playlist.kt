package com.practicum.playlistmaker.creationplaylist.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val pathToCover: String,
    val tracksId: List<String>,
    val size: String
): Parcelable
