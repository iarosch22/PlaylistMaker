package com.practicum.playlistmaker.search.data.dto

class TrackSearchResponse(val resultCount: String, val results: List<TrackDto>): Response() {}