package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.creationplaylist.ui.CreationPlaylistFragment
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.library.ui.playlists.PlaylistsFragment.Companion.NEW_PLAYLIST_MODE
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

const val SDK_TIRAMISU = Build.VERSION_CODES.TIRAMISU

class PlayerFragment : Fragment() {

    private var track: Track? = null

    private val playlists = mutableListOf<Playlist>()

    private val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }

    private val timeFormatter by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var binding: FragmentPlayerBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val playerAdapter by lazy { PlayerAdapter( createOnPlaylistListener() ) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackBtn()

        setTrackValues()

        setBottomSheet()

        setPlaylistsAdapter()

        binding.saveToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            renderState(state)
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

        binding.newPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_playerFragment_to_creationPlaylistFragment,
                CreationPlaylistFragment.createArgs(NEW_PLAYLIST_MODE)
            )
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onPause()
    }

    private fun setBackBtn() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
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
            arguments?.getParcelable(TRACK, Track::class.java)
        } else {
            arguments?.getParcelable(TRACK)
        } ?: run {
            findNavController().popBackStack()
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
        val cornersValuePx = dpToPx(cornersValueDp, requireContext())

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
        val playlistListener = OnPlaylistClickListener { name, tracksId: List<String> ->
            viewModel.hasTrackInPlaylist(name, tracksId)
        }

        return playlistListener
    }

    private fun renderState(state: PlayerUiState) {
        when (state) {
            is PlayerUiState.Playing -> {
                binding.playButton.setImageResource(R.drawable.ic_pause)
                binding.duration.text = state.progress
                setFavoriteIcon(state.isFavorite)
            }

            is PlayerUiState.Paused -> {
                binding.playButton.setImageResource(R.drawable.ic_play)
                binding.duration.text = state.progress
                setFavoriteIcon(state.isFavorite)
            }

            is PlayerUiState.Prepared -> {
                binding.playButton.setImageResource(R.drawable.ic_play)
                binding.duration.text = DEFAULT_TIMER
            }

            is PlayerUiState.Default -> {
                binding.playButton.setImageResource(R.drawable.ic_play)
                binding.duration.text = DEFAULT_TIMER
            }

            is PlayerUiState.AddingToPlaylist -> {
                if (state.isSuccess) {
                    Toast.makeText(requireContext(), "Добавлено в плейлист [${state.playlistName}]", Toast.LENGTH_SHORT).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    Toast.makeText(requireContext(), "Трек уже добавлен в плейлист [${state.playlistName}]", Toast.LENGTH_SHORT).show()
                }
            }
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

        private const val DEFAULT_TIMER = "00:00"
    }

}