package com.practicum.playlistmaker.player.ui

fun interface OnPlaylistClickListener {

    fun onPlaylistClick(playlistName: String, tracksId: List<String>)

}