package com.practicum.playlistmaker.aboutplaylist.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AboutPlaylistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val ivTrackArtwork: ImageView = itemView.findViewById(R.id.ivTrackArtwork)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)

    fun bind(item: Track) {
        val trackTime = item.trackTimeMillis.toLong()

        tvTrackName.text = item.trackName
        tvArtistName.text = item.artistName
        tvTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
        loadTrackArtwork(item.artworkUrl100)
    }

    private fun loadTrackArtwork(artworkUrl: String?) {
        val cornersValueDp = 2F
        val cornersValuePx = dpToPx(cornersValueDp, itemView.context)

        Glide.with(itemView)
            .load(artworkUrl)
            .centerCrop()
            .transform(RoundedCorners(cornersValuePx))
            .placeholder(R.drawable.ic_placeholder)
            .into(ivTrackArtwork)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

}