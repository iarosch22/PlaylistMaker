package com.practicum.playlistmaker.library.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.creationplaylist.ui.CreationPlaylistFragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.ui.view_models.PlaylistsViewModel
import com.practicum.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: BindingFragment<FragmentPlaylistsBinding>() {

    private val viewModel: PlaylistsViewModel by viewModel()

    private val playlists = mutableListOf<Playlist>()

    private val adapter by lazy { PlaylistsAdapter() }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter.playlist = playlists
        binding.rvPlaylists.adapter = adapter

        binding.addPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_libraryFragment_to_creationPlaylistFragment,
                CreationPlaylistFragment.createArgs(NEW_PLAYLIST_MODE)
            )
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            when(it) {
                is PlaylistsUiState.Content -> showContent(it.playlists)
                PlaylistsUiState.Empty -> showMessage()
            }
        }
    }

    private fun showContent(playlistsFromDb: List<Playlist>) {
        binding.placeholder.visibility = View.GONE
        binding.rvPlaylists.visibility = View.VISIBLE
        playlists.clear()
        playlists.addAll(playlistsFromDb)
        adapter.notifyDataSetChanged()
    }

    private fun showMessage() {
        binding.placeholder.visibility = View.VISIBLE
        binding.placeholder.visibility = View.GONE
    }

    companion object {
        const val NEW_PLAYLIST_MODE = -1L

        fun newInstance(): PlaylistsFragment = PlaylistsFragment().apply {  }
    }

}