package com.practicum.playlistmaker.ui.tracks

import com.practicum.playlistmaker.domain.models.Track

fun interface OnTrackClickListener {

    fun onTrackClick(track: Track)

}