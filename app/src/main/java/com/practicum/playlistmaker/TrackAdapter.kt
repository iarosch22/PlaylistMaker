package com.practicum.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TrackAdapter(context: Context): RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()
    private val sharedPreferences = context.getSharedPreferences(APP_SEARCH_HISTORY, Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_search_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {

            sharedPreferences.edit()
                .putString(APP_NEW_TRACK_KEY, Gson().toJson(tracks[position]))
                .apply()
        }
    }

}