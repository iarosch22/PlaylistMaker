package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist

class PlayerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val cover: ImageView = itemView.findViewById(R.id.ivPlaylistArtwork)
    private val name: TextView = itemView.findViewById(R.id.tvPlaylistName)
    private val size: TextView = itemView.findViewById(R.id.tvSize)

    fun bind(item: Playlist) {
        val tracksSize = item.size

        val cornersValueDp = 2F
        val cornersValuePx = dpToPx(cornersValueDp, itemView.context)

        name.text = item.name
        size.text = itemView.context.getString(R.string.app_tracks_size, tracksSize, getTrackDeclension(tracksSize.toInt()))

        Glide
            .with(itemView)
            .load(item.pathToCover)
            .centerCrop()
            .transform(RoundedCorners(cornersValuePx))
            .placeholder(R.drawable.ic_placeholder)
            .into(cover)

    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    private fun getTrackDeclension(count: Int): String {
        return when {
            count % 100 in 11..14 -> "треков"
            count % 10 == 1 -> "трек"
            count % 10 in 2..4 -> "трека"
            else -> "треков"
        }
    }

}