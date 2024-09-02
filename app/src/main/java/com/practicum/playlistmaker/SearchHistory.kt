package com.practicum.playlistmaker

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun addTrackToSearchHistory(track: Track) {
        sharedPreferences.edit()
            .putString(APP_NEW_TRACK_KEY, createJsonFromTrack(track))
            .apply()
    }

    fun saveToSearchHistory(tracks: Array<Track>) {
        sharedPreferences.edit()
            .putString(APP_SEARCH_TRACKS_KEY, createJsonFromTrackList(tracks))
            .apply()
    }

    fun readTracksFromSearhHistory(): Array<Track> {
        val json = sharedPreferences.getString(APP_SEARCH_TRACKS_KEY, null) ?: return emptyArray()
        return createTracksListFromJson(json)
    }

    private fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }

    private fun createTrackFromJson(json: String): Track {
        return Gson().fromJson(json, Track::class.java)
    }

    private fun createTracksListFromJson(json: String): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    private fun createJsonFromTrackList(tracks: Array<Track>): String {
        return Gson().toJson(tracks)
    }

}