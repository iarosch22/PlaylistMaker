package com.practicum.playlistmaker

import com.practicum.playlistmaker.domain.models.Track

fun interface OnTrackClickListener {

    fun onTrackClick(track: Track)

}