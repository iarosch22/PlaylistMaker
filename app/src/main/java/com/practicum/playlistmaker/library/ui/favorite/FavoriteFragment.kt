package com.practicum.playlistmaker.library.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding
import com.practicum.playlistmaker.library.ui.view_models.FavoriteViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.BindingFragment
import com.practicum.playlistmaker.search.ui.OnTrackClickListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment: BindingFragment<FragmentFavoriteBinding>() {

    private val tracks = mutableListOf<Track>()

    private val adapter by lazy { FavoriteAdapter(createOnTrackClick()) }

    private val favoriteViewModel: FavoriteViewModel by viewModel()

    private var isClickAllowed = true

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteBinding {
        return FragmentFavoriteBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.tracks = tracks
        binding.rvFavoriteTracks.adapter = adapter

        favoriteViewModel.observeState().observe(viewLifecycleOwner) {
            when(it) {
                is FavoriteState.Content -> showContent(favoriteTracks = it.favoriteTracks)
                FavoriteState.Empty -> showMessage()
            }
        }
    }


    private fun showContent(favoriteTracks: List<Track>) {
        binding.rvFavoriteTracks.visibility = View.VISIBLE
        binding.phEmpty.visibility = View.GONE
        tracks.clear()
        tracks.addAll(favoriteTracks)
        adapter.notifyDataSetChanged()
    }

    private fun showMessage() {
        binding.rvFavoriteTracks.visibility = View.GONE
        binding.phEmpty.visibility = View.VISIBLE
    }

    private fun createOnTrackClick(): OnTrackClickListener {
        val trackListener = OnTrackClickListener { track: Track ->
            if (clickDebounce()) {
                findNavController().navigate(
                    R.id.action_libraryFragment_to_playerActivity,
                    bundleOf(TRACK to track)
                )
            }
        }

        return trackListener
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed

        if (isClickAllowed) {
            isClickAllowed = false

            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                isClickAllowed = true
            }
        }

        return current
    }

    companion object {
        fun newInstance(): FavoriteFragment = FavoriteFragment().apply {  }

        private const val TRACK = "TRACK"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }

}