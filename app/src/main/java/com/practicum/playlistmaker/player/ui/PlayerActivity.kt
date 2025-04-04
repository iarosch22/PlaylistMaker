package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
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

    private var track: Track? = null

    private val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }

    private val timeFormatter by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setBackBtn()

        setTrackValues()

        viewModel.observeState().observe(this) {
            binding.duration.text = it.progress
            if (it is PlayerUiState.Playing) {
                binding.playButton.setImageResource(R.drawable.ic_pause)
                setFavoriteIcon(it.isFavorite)
            } else {
                binding.playButton.setImageResource(R.drawable.ic_play)
                setFavoriteIcon(it.isFavorite)
            }

        }

        binding.playButton.setOnClickListener{
            viewModel.onPlayButtonClicked()
        }

        binding.saveToFavorites.setOnClickListener { viewModel.onFavoriteClicked() }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onPause()
    }

    private fun setBackBtn() {
        binding.btnBack.setOnClickListener {
            this.finish()
        }
    }

    private fun setTrackValues() {
        track = if (VERSION.SDK_INT >= SDK_TIRAMISU) {
            intent.getParcelableExtra(TRACK, Track::class.java)
        } else {
            intent.getParcelableExtra(TRACK)
        }

        if (track == null) {
            finish()
            return
        }

        binding.trackName.text = track!!.trackName
        binding.artistName.text = track!!.artistName
        binding.trackTimeValue.text = timeFormatter.format(track!!.trackTimeMillis.toLong())

        if (track!!.collectionName.isEmpty()) {
            binding.collectionNameValue.visibility = View.GONE
            binding.collectionName.visibility = View.GONE
        } else {
            binding.collectionNameValue.text = track!!.collectionName
        }

        binding.releaseDateValue.text = track!!.releaseDate.take(4).takeIf { it.length == 4 } ?: ""
        binding.primaryGenreNameValue.text = track!!.primaryGenreName
        binding.countryValue.text = track!!.country

        if (track!!.isFavorite) {
            binding.saveToFavorites.setImageResource(R.drawable.ic_savetofavorite_active)
        } else {
            binding.saveToFavorites.setImageResource(R.drawable.ic_savetofavorite_inactive)
        }

        loadArtWork(track!!.getCoverArtwork())
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

    private fun setFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.saveToFavorites.setImageResource(R.drawable.ic_savetofavorite_active)
        } else {
            binding.saveToFavorites.setImageResource(R.drawable.ic_savetofavorite_inactive)
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    companion object {
        private const val TRACK = "TRACK"
    }

}