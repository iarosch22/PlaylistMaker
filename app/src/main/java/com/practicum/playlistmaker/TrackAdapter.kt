package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TrackAdapter(private val context: Context): RecyclerView.Adapter<TrackViewHolder>() {

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
            Toast.makeText(holder.itemView.context, tracks[position].trackId, Toast.LENGTH_SHORT).show()

            sharedPreferences.edit()
                .putString(APP_NEW_TRACK_KEY, Gson().toJson(tracks[position]))
                .apply()
        }
    }

}