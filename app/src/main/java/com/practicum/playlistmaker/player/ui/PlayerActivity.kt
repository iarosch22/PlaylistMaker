package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.library.ui.playlists.PlaylistsAdapter
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

const val SDK_TIRAMISU = Build.VERSION_CODES.TIRAMISU

class PlayerActivity : AppCompatActivity() {

    private var track: Track? = null

    private val playlists = mutableListOf<Playlist>()

    private val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }

    private val timeFormatter by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val playerAdapter by lazy { PlayerAdapter( createOnPlaylistListener() ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setBackBtn()

        setTrackValues()

        setBottomSheet()

        setPlaylistsAdapter()

        binding.saveToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observePlaylistsState()
                    .collect { list ->
                        playlists.clear()
                        playlists.addAll(list)
                        playerAdapter.notifyDataSetChanged()
                    }
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

    private fun setPlaylistsAdapter() {
        playerAdapter.playlists = playlists
        binding.rvBottomSheetPlaylist.adapter = playerAdapter
    }

    private fun setBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    } else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        })
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

    private fun createOnPlaylistListener(): OnPlaylistClickListener {
        val playlistListener = OnPlaylistClickListener { tracksId: List<String> ->
            if (viewModel.hasTrackInPlaylist(tracksId)) {
                Toast.makeText(this@PlayerActivity, "TRACK IS IN PLAYLIST", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@PlayerActivity, "TRACK IS NOT IN PLAYLIST", Toast.LENGTH_SHORT).show()
            }
        }

        return playlistListener
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