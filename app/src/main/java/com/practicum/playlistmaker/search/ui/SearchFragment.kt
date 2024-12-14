package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.ErrorMessageType
import com.practicum.playlistmaker.search.ui.view_model.TracksSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {

    private var textValue = TEXT_DEF

    private lateinit var binding: FragmentSearchBinding

    private var isClickAllowed = true

    private val searchedTracks = ArrayList<Track>()
    private val savedTracks = ArrayList<Track>()
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var searchTracksAdapter: TracksAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var textWatcher: TextWatcher? = null

    private val viewModel by viewModel<TracksSearchViewModel>()

    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputMethodManager = (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)!!

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        setAdapters()

        setButtons()

        setTextWatcher()

        showLatestSearch(false)

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus -> showLatestSearch(hasFocus) }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
    }

    override fun onStop() {
        super.onStop()

        viewModel.saveSearchedTracks(searchTracksAdapter.tracks)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, textValue)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            textValue = savedInstanceState.getString(
                INPUT_TEXT,
                TEXT_DEF
            )
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

    }

    private fun setAdapters() {
        tracksAdapter = TracksAdapter(createOnTrackClick())
        searchTracksAdapter = TracksAdapter(createOnTrackClick())

        tracksAdapter.tracks = searchedTracks
        binding.rvTrackSearch.adapter = tracksAdapter

        searchTracksAdapter.tracks = savedTracks
        binding.rvLatestTrackSearch.adapter = searchTracksAdapter
    }

    private fun setBtnClear() {
        binding.clearInputBtn.setOnClickListener {
            binding.inputEditText.setText(TEXT_DEF)
            searchedTracks.clear()
            tracksAdapter.notifyDataSetChanged()
            showMessage(ErrorMessageType.NO_MESSAGE)
            inputMethodManager.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
        }
    }
    private fun setBtnReload() {
        binding.reloadBtn.setOnClickListener {
            if (binding.inputEditText.text.isNotEmpty()) {
                viewModel.searchDebounce(changedText = binding.inputEditText.text.toString())
            }
        }
    }
    private fun setClearHistoryBtn() {
        binding.clearSearchHistory.setOnClickListener {
            savedTracks.clear()
            binding.latestSearchList.visibility = View.GONE
            searchTracksAdapter.notifyDataSetChanged()
        }
    }
    private fun setButtons() {
        setBtnClear()
        setBtnReload()
        setClearHistoryBtn()
    }

    private fun setTextWatcher() {
        textWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textValue = s.toString()
                binding.clearInputBtn.visibility = clearButtonVisibility(s)
                if (s != null) {
                    binding.latestSearchList.visibility = if (
                        binding.inputEditText.hasFocus() &&
                        s.isEmpty() &&
                        savedTracks.isNotEmpty()
                        ) {
                        showMessage(ErrorMessageType.NO_MESSAGE)
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                    viewModel.searchDebounce(changedText = s.toString())
                }

            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.inputEditText.addTextChangedListener(textWatcher)
    }

    private fun addSearchTrack(track: Track) {
        val duplicateTrackIndex = savedTracks.indexOfFirst { it.trackId == track.trackId }

        if (duplicateTrackIndex !=  -1) {
            deleteSearchTrack(duplicateTrackIndex)
        }

        if (savedTracks.size == LATEST_SEARCH_TRACKS_SIZE) {
            val positionToRemove = LATEST_SEARCH_TRACKS_SIZE - 1

            deleteSearchTrack(positionToRemove)
        }

        savedTracks.add(0, track)
        searchTracksAdapter.notifyDataSetChanged()
    }

    private fun deleteSearchTrack(position: Int) {
        savedTracks.removeAt(position)
        searchTracksAdapter.notifyItemRemoved(position)
        searchTracksAdapter.notifyItemRangeChanged(position, savedTracks.size)
    }

    private fun showLatestSearch(hasFocus: Boolean) {
        binding.latestSearchList.visibility = if (
            hasFocus &&
            binding.inputEditText.text.isEmpty() &&
            searchTracksAdapter.tracks.isNotEmpty()
            ) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun createOnTrackClick(): OnTrackClickListener {
        val trackListener = OnTrackClickListener { track: Track ->
            addSearchTrack(track)

            if (clickDebounce()) {
                findNavController().navigate(
                    R.id.action_searchFragment_to_playerActivity,
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
            handler.postDelayed({ isClickAllowed = true},
                CLICK_DEBOUNCE_DELAY_MILLIS
            )
        }
        return current
    }

    private fun showMessage(type: ErrorMessageType) {
        when(type) {
            ErrorMessageType.SOMETHING_WENT_WRONG -> {
                binding.phNothingFound.visibility = View.GONE
                binding.phSomethingWentWrong.visibility = View.VISIBLE
            }
            ErrorMessageType.NOTHING_FOUND -> {
                binding.phSomethingWentWrong.visibility = View.GONE
                binding.phNothingFound.visibility = View.VISIBLE
            }
            ErrorMessageType.NO_MESSAGE -> {
                binding.phSomethingWentWrong.visibility = View.GONE
                binding.phNothingFound.visibility = View.GONE
            }
        }

        binding.progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvTrackSearch.visibility = View.GONE
    }

    private fun showContent(foundTracks: List<Track>) {
        binding.progressBar.visibility = View.GONE
        searchedTracks.clear()
        searchedTracks.addAll(foundTracks)
        binding.rvTrackSearch.visibility = View.VISIBLE
        tracksAdapter.notifyDataSetChanged()
    }

    private fun render(state: TracksState) {
        when(state) {
            is TracksState.SearchedContent -> showContent(state.searchedTracks)
            is TracksState.HistoryContent -> addSearchedTracks(state.savedTracks)
            is TracksState.Error -> showMessage(state.errorMessage)
            TracksState.Loading -> showLoading()
        }
    }

    private fun addSearchedTracks(tracks: List<Track>) {
        savedTracks.addAll(tracks)
        searchTracksAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val TRACK = "TRACK"
        private const val INPUT_TEXT = "INPUT_TEXT"
        private const val TEXT_DEF = ""
        private const val LATEST_SEARCH_TRACKS_SIZE = 10
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }

}