package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist

fun interface OnAddToPlaylistClickListener {

    fun onPlaylistClick(playlist: Playlist, tracksId: List<String>)

}