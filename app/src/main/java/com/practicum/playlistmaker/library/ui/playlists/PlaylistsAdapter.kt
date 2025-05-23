package com.practicum.playlistmaker.library.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist

class PlaylistsAdapter(private val listener: OnOpenPlaylistClickListener): RecyclerView.Adapter<PlaylistsViewHolder>() {

    var playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_library_item, parent, false)
        return PlaylistsViewHolder(view)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])

        holder.itemView.setOnClickListener {
            listener.onPlaylistClick(playlists[position].id)
        }
    }

}