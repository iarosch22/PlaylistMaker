package com.practicum.playlistmaker.aboutplaylist.ui

import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

fun interface OnTrackLongClickListener {

    fun onTrackLongClick(track: Track)

}