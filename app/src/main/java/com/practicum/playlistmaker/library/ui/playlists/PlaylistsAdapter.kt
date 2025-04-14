package com.practicum.playlistmaker.library.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist

class PlaylistsAdapter: RecyclerView.Adapter<PlaylistsViewHolder>() {

    var playlist = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_library_item, parent, false)
        return PlaylistsViewHolder(view)
    }

    override fun getItemCount(): Int = playlist.size

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlist[position])
    }

}