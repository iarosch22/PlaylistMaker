package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.domain.models.Track

class TrackSearchResponse(val resultCount: String, val results: List<Track>) {}