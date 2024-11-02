package com.practicum.playlistmaker.search.ui.activity

import com.practicum.playlistmaker.search.domain.models.Track

fun interface OnTrackClickListener {

    fun onTrackClick(track: Track)

}