package com.practicum.playlistmaker.aboutplaylist.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.aboutplaylist.ui.view_model.AboutPlaylistAdapter
import com.practicum.playlistmaker.aboutplaylist.ui.view_model.AboutPlaylistViewModel
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.databinding.FragmentAboutplaylistBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.OnTrackClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AboutPlaylistFragment: Fragment() {

    private val playlistTracks = mutableListOf<Track>()

    private lateinit var binding: FragmentAboutplaylistBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val adapter by lazy { AboutPlaylistAdapter(
        createOnTrackClick(),
        createOnTrackLongClick()
    ) }

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

        setAdapter()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
        }

            binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }

        viewModel.observeAboutPlaylist().observe(viewLifecycleOwner) {
            Log.d("AboutPlaylistFragment", "renderState called")
            renderState(it)
        }
    }

    private fun setAdapter() {
        adapter.tracks = playlistTracks
        binding.rvBottomSheetTracks.adapter = adapter
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

        setCoverImage(playlist.pathToCover.toUri())

        setTracks(tracks)
    }

    private fun setTracks(tracks: List<Track>) {
        playlistTracks.clear()
        playlistTracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun getDuration(tracks: List<Track>): String {
        val totalDuration = tracks.sumOf { it.trackTimeMillis.toLong() }
        return SimpleDateFormat("mm", Locale.getDefault()).format(totalDuration)
    }

    private fun setCoverImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder_large)
            .into(binding.cover)
    }

    private fun createOnTrackClick(): OnTrackClickListener {
        val listener = OnTrackClickListener { track: Track ->

            findNavController().navigate(
                R.id.action_aboutPlaylistFragment_to_playerFragment,
                bundleOf(TRACK to track)
            )
        }

        return listener
    }

    private fun createOnTrackLongClick(): OnTrackLongClickListener {
        val listener = OnTrackLongClickListener { track: Track ->
            showConfirmation(track)
        }

        return listener
    }

    private fun showConfirmation(track: Track) {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.app_delete_dialog_message))
            .setNegativeButton(getString(R.string.app_delete_dialog_cancel)) { _, _ -> }
            .setPositiveButton(getString(R.string.app_delete_dialog_confirm)) { _, _ ->
                viewModel.deleteTrack(track)
            }

        confirmDialog.show()
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

        private const val TRACK = "TRACK"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)

    }

}