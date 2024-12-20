package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

const val SDK_TIRAMISU = Build.VERSION_CODES.TIRAMISU

class PlayerActivity : AppCompatActivity() {

    private var trackUrl: String = ""

    private val viewModel by viewModel<PlayerViewModel> { parametersOf(trackUrl) }

    private val timeFormatter by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setBackBtn()

        setTrackValues()

        viewModel.observeState().observe(this) { state ->
            handleStateChange(state)
        }

        binding.playButton.setOnClickListener{
            playbackControl()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun setBackBtn() {
        binding.btnBack.setOnClickListener {
            this.finish()
        }
    }

    private fun setTrackValues() {
        val track = if (VERSION.SDK_INT >= SDK_TIRAMISU) {
            intent.getParcelableExtra(TRACK, Track::class.java)
        } else {
            intent.getParcelableExtra(TRACK)
        }

        if (track == null) {
            finish()
            return
        }

        trackUrl = track.previewUrl

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTimeValue.text = timeFormatter.format(track.trackTimeMillis.toLong())

        if (track.collectionName.isEmpty()) {
            binding.collectionNameValue.visibility = View.GONE
            binding.collectionName.visibility = View.GONE
        } else {
            binding.collectionNameValue.text = track.collectionName
        }

        binding.releaseDateValue.text = track.releaseDate.take(4).takeIf { it.length == 4 } ?: ""
        binding.primaryGenreNameValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

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
            .into(binding.cover)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    private fun startPlayer() {
        viewModel.startPlayer()
        binding.playButton.setImageResource(R.drawable.ic_pause)
    }

    private fun pausePlayer() {
        viewModel.pausePlayer()
        binding.playButton.setImageResource(R.drawable.ic_play)
    }

    private fun playbackControl() {
        when (viewModel.observeState().value) {
            is PlayerUiState.Playing -> viewModel.pausePlayer()
            else -> {
                viewModel.startPlayer()
            }
        }
    }

    private fun handleStateChange(state: PlayerUiState) {
        when(state) {
            PlayerUiState.Prepared -> {
                binding.duration.text = getString(R.string.app_duration)
            }
            is PlayerUiState.Playing -> {
                startPlayer()
                binding.duration.text = timeFormatter.format(state.trackTime)
            }
            PlayerUiState.Pause -> {
                pausePlayer()
            }
            PlayerUiState.Default -> {
                pausePlayer()
                binding.duration.text = getString(R.string.app_duration)
            }
        }
    }

    companion object {
        private const val TRACK = "TRACK"
    }

}