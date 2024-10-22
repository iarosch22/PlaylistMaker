package com.practicum.playlistmaker.ui.player

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.ui.tracks.TRACK
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

const val SDK_TIRAMISU = Build.VERSION_CODES.TIRAMISU

class PlayerActivity : AppCompatActivity() {

    private val mediaPlayer = MediaPlayer()

    private var playerState = STATE_DEFAULT

    private var url: String = ""

    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val runnableDuration by lazy { createAndUpdateDuration() }

    private lateinit var binding: ActivityPlayerBinding

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
    private lateinit var playButton: ImageView
    private lateinit var duration: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        backBtn = binding.btnBack
        cover = binding.cover
        trackName = binding.trackName
        artistName = binding.artistName
        trackTimeValue = binding.trackTimeValue
        collectionNameField = binding.collectionName
        collectionNameValue = binding.collectionNameValue
        releaseDateValue = binding.releaseDateValue
        primaryGenreNameValue = binding.primaryGenreNameValue
        countryValue = binding.countryValue
        playButton = binding.playButton
        duration = binding.duration

        setBackBtn()

        setTrackValues()

        preparePlayer()

        playButton.setOnClickListener{
            playbackControl()
        }
    }

    override fun onStop() {
        super.onStop()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(runnableDuration)
    }

    private fun setBackBtn() {
        backBtn.setOnClickListener {
            this.finish()
        }
    }

    private fun setTrackValues() {
        val track = if (VERSION.SDK_INT >= SDK_TIRAMISU) {
            intent.getParcelableExtra(TRACK, Track::class.java)
        } else {
            intent.getParcelableExtra<Track>(TRACK)
        }

        if (track == null) {
            finish()
            return
        }

        url = track.previewUrl
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTimeValue.text = dateFormat.format(track.trackTimeMillis.toLong())

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

    private fun preparePlayer() {
        if (url.isNotEmpty()) {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
            }

            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                playButton.setImageResource(R.drawable.ic_play)
                handler.removeCallbacks(createAndUpdateDuration())
                duration.text = getString(R.string.app_duration)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        handler.post(runnableDuration)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun createAndUpdateDuration(): Runnable {
        return object : Runnable {
            override fun run() {
                when(playerState) {
                    STATE_PLAYING -> {
                        duration.text =SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                        handler.postDelayed(this, CHECK_INTERVAL)
                    }
                    STATE_PAUSED -> {
                        handler.removeCallbacks(this)
                    }
                }
            }

        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val CHECK_INTERVAL = 300L
    }

}