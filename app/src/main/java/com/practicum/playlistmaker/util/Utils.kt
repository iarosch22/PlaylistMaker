package com.practicum.playlistmaker.util

import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale


fun getTrackDeclension(count: Int): String {
    return when {
        count % 100 in 11..14 -> "треков"
        count % 10 == 1 -> "трек"
        count % 10 in 2..4 -> "трека"
        else -> "треков"
    }
}

fun getDuration(tracks: List<Track>): String {
    val totalDuration = tracks.sumOf { it.trackTimeMillis.toLong() }
    return SimpleDateFormat("mm", Locale.getDefault()).format(totalDuration)
}