package com.practicum.playlistmaker.aboutplaylist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.OnTrackClickListener

class AboutPlaylistAdapter(
    private val listener: OnTrackClickListener,
    private val longClickListener: OnTrackLongClickListener
): RecyclerView.Adapter<AboutPlaylistViewHolder>() {

    var tracks = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutPlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_search_item, parent, false)
        return AboutPlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: AboutPlaylistViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            listener.onTrackClick(tracks[position])
        }

        holder.itemView.setOnLongClickListener {
            longClickListener.onTrackLongClick(tracks[position])
            true
        }
    }

}