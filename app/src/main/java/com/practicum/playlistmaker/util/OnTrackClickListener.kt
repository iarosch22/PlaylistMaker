package com.practicum.playlistmaker.util

import com.practicum.playlistmaker.search.domain.models.Track

fun interface OnTrackClickListener {

    fun onTrackClick(track: Track)

}