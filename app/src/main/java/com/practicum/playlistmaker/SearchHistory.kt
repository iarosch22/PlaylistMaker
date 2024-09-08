package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {

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

    fun createTrackFromJson(json: String): Track {
        return gson.fromJson(json, Track::class.java)
    }

    fun clearHistory() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    private fun createJsonFromTrackList(tracks: ArrayList<Track>): String {
        return gson.toJson(tracks)
    }

}