package com.practicum.playlistmaker

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

const val SDK_TIRAMISU = Build.VERSION_CODES.TIRAMISU

class PlayerActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageButton
    private lateinit var cover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTimeValue: TextView
    private lateinit var collectionNameField: TextView
    private lateinit var collectionNameValue: TextView
    private lateinit var releaseDateValue: TextView
    private lateinit var primaryGenreNameValue: TextView
    private lateinit var countryValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        backBtn = findViewById(R.id.btn_back)
        cover = findViewById(R.id.cover)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTimeValue = findViewById(R.id.trackTimeValue)
        collectionNameField = findViewById(R.id.collectionName)
        collectionNameValue = findViewById(R.id.collectionNameValue)
        releaseDateValue = findViewById(R.id.releaseDateValue)
        primaryGenreNameValue = findViewById(R.id.primaryGenreNameValue)
        countryValue = findViewById(R.id.countryValue)

        setBackBtn()

        setTrackValues()
    }

    private fun setBackBtn() {
        backBtn.setOnClickListener {
            this.finish()
        }
    }

    private fun setTrackValues() {
        val track = if (VERSION.SDK_INT >= SDK_TIRAMISU) {
            intent.getParcelableExtra("TRACK", Track::class.java)
        } else {
            intent.getParcelableExtra<Track>("TRACK")
        }

        if (track == null) {
            finish()
            return
        }

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTimeValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())

        if (track.collectionName.isEmpty()) {
            collectionNameValue.visibility = View.GONE
            collectionNameField.visibility = View.GONE
        } else {
            collectionNameValue.text = track.collectionName
        }

        releaseDateValue.text = track.releaseDate.take(4).takeIf { it.length == 4 } ?: ""
        primaryGenreNameValue.text = track.primaryGenreName
        countryValue.text = track.country

        loadArtWork(track.getCoverArtwork())
    }

    private fun loadArtWork(artworkUrl: String) {
        val cornersValueDp = 8F
        val cornersValuePx = dpToPx(cornersValueDp, this)

        Glide.with(this)
            .load(artworkUrl)
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

}