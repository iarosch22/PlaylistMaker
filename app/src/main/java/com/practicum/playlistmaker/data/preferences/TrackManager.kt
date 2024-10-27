package com.practicum.playlistmaker.data.preferences

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.models.Track

const val APP_SEARCH_HISTORY = "app_search_history"
const val APP_SEARCH_TRACKS_KEY = "app_search_tracks_key"
const val APP_NEW_TRACK_KEY = "app_new_track_key"

class TrackManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(APP_SEARCH_HISTORY, Context.MODE_PRIVATE)
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

    fun addTrackToHistory(track: Track) {
        sharedPreferences.edit()
            .putString(APP_NEW_TRACK_KEY, Gson().toJson(track))
            .apply()
    }

    fun clearHistory() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    private fun createTrackFromJson(json: String): Track {
        return gson.fromJson(json, Track::class.java)
    }

    private fun createJsonFromTrackList(tracks: ArrayList<Track>): String {
        return gson.toJson(tracks)
    }

}