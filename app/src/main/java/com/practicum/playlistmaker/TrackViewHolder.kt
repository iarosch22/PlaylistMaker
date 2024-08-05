package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val rootLayout: LinearLayout = itemView.findViewById(R.id.rootLayout)
    private val ivTrackArtwork: ImageView = itemView.findViewById(R.id.ivTrackArtwork)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)

    fun bind(item: Track) {
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .into(ivTrackArtwork)
    }
}