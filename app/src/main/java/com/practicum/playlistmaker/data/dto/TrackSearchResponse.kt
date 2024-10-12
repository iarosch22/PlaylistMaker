package com.practicum.playlistmaker.data.dto

class TrackSearchResponse(val resultCount: String, val results: List<TrackDto>): Response() {}