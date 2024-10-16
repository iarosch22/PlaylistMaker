package com.practicum.playlistmaker.data

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.tracks.APP_SEARCH_TRACKS_KEY

class TrackManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("app_search_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveToSearchHistory(tracks: ArrayList<Track>) {
        sharedPreferences.edit()
            .putString(APP_SEARCH_TRACKS_KEY, createJsonFromTrackList(tracks))
            .apply()
    }

    fun readTracksFromSearchHistory(): ArrayList<Track> {
        val json = sharedPreferences.getString(APP_SEARCH_TRACKS_KEY, null) ?: return ArrayList()
        val array = gson.fromJson(json, Array<Track>::class.java)
        return ArrayList(array.toList())
    }

    fun clearHistory() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    fun createTrackFromJson(json: String): Track {
        return gson.fromJson(json, Track::class.java)
    }

    private fun createJsonFromTrackList(tracks: ArrayList<Track>): String {
        return gson.toJson(tracks)
    }

}