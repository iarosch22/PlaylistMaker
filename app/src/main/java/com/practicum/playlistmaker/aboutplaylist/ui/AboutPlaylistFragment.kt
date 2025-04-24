package com.practicum.playlistmaker.aboutplaylist.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.aboutplaylist.ui.view_model.AboutPlaylistViewModel
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.creationplaylist.ui.CreationPlaylistFragment
import com.practicum.playlistmaker.databinding.FragmentAboutplaylistBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.OnTrackClickListener
import com.practicum.playlistmaker.util.getDuration
import com.practicum.playlistmaker.util.getTrackDeclension
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AboutPlaylistFragment: Fragment() {

    private val playlistTracks = mutableListOf<Track>()

    private lateinit var binding: FragmentAboutplaylistBinding

    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var optionBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var deleteTrackConfirmDialog: MaterialAlertDialogBuilder

    private lateinit var deletePlaylistConfirmDialog: MaterialAlertDialogBuilder

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

        setBottomSheets()

        viewModel.observeAboutPlaylist().observe(viewLifecycleOwner) {
            renderState(it)
        }

        viewModel.observeEditMode().observe(viewLifecycleOwner) { state ->
            if (state is EditModeState.Show) {
                findNavController().navigate(
                    R.id.action_aboutPlaylistFragment_to_creationPlaylistFragment,
                    CreationPlaylistFragment.createArgs(state.playlistId)
                )
                viewModel.editModeStarted()
            }
        }

        binding.optionsBtn.setOnClickListener {
            optionBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.shareBtn.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.sharePlaylist.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.deletePlaylist.setOnClickListener{
            viewModel.showDeletePlaylistDialog()
        }

        binding.editPlaylist.setOnClickListener {
            viewModel.startEditingPlaylist()
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

        setTracksToBottomSheet(tracks)
        setOptionsBottomSheet(playlist)
    }

    private fun setTracksToBottomSheet(tracks: List<Track>) {
        playlistTracks.clear()
        playlistTracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun setBottomSheets() {
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
        }
        tracksBottomSheetBehavior.isDraggable = true

        optionBottomSheetBehavior = BottomSheetBehavior.from(binding.optionsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        optionBottomSheetBehavior.isDraggable = true
        optionBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

    private fun setOptionsBottomSheet(playlist: Playlist) {
        val cornersValueDp = 2F
        val cornersValuePx = dpToPx(cornersValueDp, requireContext())

        binding.bsNamePlaylist.text = playlist.name
        binding.bsSizePlaylist.text = requireContext().getString(
            R.string.app_tracks_size, playlist.size, getTrackDeclension(playlist.size.toInt())
        )
        Glide.with(this)
            .load(playlist.pathToCover)
            .transform(
                CenterCrop(),
                RoundedCorners(cornersValuePx)
            )
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.bsPlaylistCover)
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
            showDeleteTrackConfirmation(track)
        }

        return listener
    }

    private fun showDeleteTrackConfirmation(track: Track) {
        deleteTrackConfirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.app_delete_track_dialog_message))
            .setNegativeButton(getString(R.string.app_delete_dialog_cancel)) { _, _ -> }
            .setPositiveButton(getString(R.string.app_delete_dialog_confirm)) { _, _ ->
                viewModel.deleteTrack(track)
            }

        deleteTrackConfirmDialog.show()
    }

    private fun showDeletePlaylistConfirmation(playlistName: String) {
        deletePlaylistConfirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.app_delete_playlist_dialog_message, playlistName))
            .setNegativeButton(getString(R.string.app_delete_dialog_cancel)) { _, _ -> }
            .setPositiveButton(getString(R.string.app_delete_dialog_confirm)) { _, _ ->
                viewModel.deletePlaylist()
                findNavController().popBackStack()
            }

        deletePlaylistConfirmDialog.show()
    }

    private fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        val title = playlist.name
        val description = playlist.description
        val size = getString(
            R.string.app_tracks_size, playlist.size, getTrackDeclension(playlist.size.toInt())
        )

        val trackList = tracks.mapIndexed { index, track ->
            val duration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
            "${index + 1}. ${track.trackName} - ${track.artistName} [$duration]"
        }.joinToString("\n")

        val fullText = """
        $title
        $description
        $size
        $trackList
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, fullText)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        requireContext().startActivity(shareIntent)
    }

    private fun renderState(state: AboutPlaylistUiState) {
        when(state) {
            is AboutPlaylistUiState.Content -> {
                setData(state.playlist, state.tracks)
            }

            is AboutPlaylistUiState.ShareContent -> {
                Log.d("AboutPlaylistUiState", "${state.isEmptyContent}")
                if (!state.isEmptyContent) {
                    sharePlaylist(state.playlist, state.tracks)
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.app_share_message,
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }

            is AboutPlaylistUiState.ShowDeletePlaylistDialog -> {
                showDeletePlaylistConfirmation(state.playlistName)
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

        private const val ARGS_PLAYLIST_ID = "playlist_id"

        private const val TRACK = "TRACK"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)

    }

}