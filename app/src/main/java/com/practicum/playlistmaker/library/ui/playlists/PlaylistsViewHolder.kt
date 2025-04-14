package com.practicum.playlistmaker.library.ui.playlists

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist

class PlaylistsViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val name: TextView = itemView.findViewById(R.id.tvNamePlaylist)
    private val size: TextView = itemView.findViewById(R.id.tvSizePlaylist)
    private val cover: ImageView = itemView.findViewById(R.id.ivPlaylistCover)

    fun bind(playlist: Playlist) {
        val tracksSize = playlist.tracksId.size

        val cornersValueDp = 8F
        val cornersValuePx = dpToPx(cornersValueDp, itemView.context)

        name.text = playlist.name
        size.text = itemView.context.getString(R.string.app_tracks_size, tracksSize, getTrackDeclension(tracksSize))

        Glide.with(itemView)
            .load(playlist.pathToCover)
            .centerCrop()
            .transform(RoundedCorners(cornersValuePx))
            .placeholder(R.drawable.ic_placeholder_large)
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