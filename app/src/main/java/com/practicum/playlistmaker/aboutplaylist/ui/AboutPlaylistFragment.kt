package com.practicum.playlistmaker.aboutplaylist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.aboutplaylist.ui.view_model.AboutPlaylistViewModel
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.databinding.FragmentAboutplaylistBinding
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AboutPlaylistFragment: Fragment() {

    private lateinit var binding: FragmentAboutplaylistBinding

    private val viewModel by viewModel<AboutPlaylistViewModel> {
        parametersOf(requireArguments().getLong(ARGS_PLAYLIST_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutplaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }

        viewModel.observeAboutPlaylist().observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    private fun setData(playlist: Playlist, tracks: List<Track>) {
        binding.playlistName.text = playlist.name
        binding.description.text = playlist.description
        binding.totalTracks.text = requireContext().getString(
            R.string.app_tracks_size, playlist.size, getTrackDeclension(playlist.size.toInt())
        )
        binding.totalDuration.text = requireContext().getString(
            R.string.app_playlist_duration, getDuration(tracks)
        )
    }

    private fun getDuration(tracks: List<Track>): String {
        val totalDuration = tracks.sumOf { it.trackTimeMillis.toLong() }
        return SimpleDateFormat("mm", Locale.getDefault()).format(totalDuration)
    }

    private fun renderState(state: AboutPlaylistUiState) {
        when(state) {
            is AboutPlaylistUiState.Content -> {
                setData(state.playlist, state.tracks)
            }
        }
    }

    private fun getTrackDeclension(count: Int): String {
        return when {
            count % 100 in 11..14 -> "треков"
            count % 10 == 1 -> "трек"
            count % 10 in 2..4 -> "трека"
            else -> "треков"
        }
    }

    companion object {

        private const val ARGS_PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)

    }

}