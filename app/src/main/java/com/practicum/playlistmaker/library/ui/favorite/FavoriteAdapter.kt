package com.practicum.playlistmaker.library.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.OnTrackClickListener

class FavoriteAdapter(private val listener: OnTrackClickListener): RecyclerView.Adapter<FavoriteViewHolder>() {

    var tracks = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_search_item, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener { listener.onTrackClick(tracks[position]) }
    }

}