package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.ErrorMessageType
import com.practicum.playlistmaker.search.ui.view_model.TracksSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

const val TRACK = "TRACK"

class SearchActivity : AppCompatActivity() {

    private var textValue = TEXT_DEF

    private var isClickAllowed = true

    private val searchedTracks = ArrayList<Track>()
    private val savedTracks = ArrayList<Track>()
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var searchTracksAdapter: TracksAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var textWatcher: TextWatcher? = null

    private val viewModel by viewModel<TracksSearchViewModel>()

    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var phSomethingWentWrong: ViewGroup
    private lateinit var phNothingFound: ViewGroup
    private lateinit var btnBack: ViewGroup
    private lateinit var hintLatestSearch: ViewGroup
    private lateinit var inputEditText: EditText
    private lateinit var clearBtn: ImageView
    private lateinit var rvTrackSearch: RecyclerView
    private lateinit var rvLatestTrack: RecyclerView
    private lateinit var reloadBtn: Button
    private lateinit var clearHistoryBtn: Button
    private lateinit var latestSearchHeading: TextView
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputMethodManager = (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)!!

        viewModel.observeState().observe(this) {
            render(it)
        }

        setViews()

        setAdapters()

        setButtons()

        setTextWatcher()

        showLatestSearch(false)
        inputEditText.setOnFocusChangeListener { _, hasFocus -> showLatestSearch(hasFocus) }
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { inputEditText.removeTextChangedListener(it) }
    }

    override fun onStop() {
        super.onStop()

        viewModel.saveSearchedTracks(searchTracksAdapter.tracks)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, textValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textValue = savedInstanceState.getString(INPUT_TEXT, TEXT_DEF)
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
        rvTrackSearch.adapter = tracksAdapter

        searchTracksAdapter.tracks = savedTracks
        rvLatestTrack.adapter = searchTracksAdapter
    }

    private fun setViews() {
        phSomethingWentWrong = findViewById(R.id.phSomethingWentWrong)
        phNothingFound = findViewById(R.id.phNothingFound)
        btnBack = findViewById(R.id.btn_back)
        inputEditText = findViewById(R.id.inputEditText)
        clearBtn = findViewById(R.id.clearIcon)
        rvTrackSearch = findViewById(R.id.rvTrackSearch)
        rvLatestTrack = findViewById(R.id.rvLatestTrackSearch)
        reloadBtn = findViewById(R.id.reloadBtn)
        hintLatestSearch = findViewById(R.id.latestSearchList)
        clearHistoryBtn = findViewById(R.id.clearSearchHistory)
        latestSearchHeading = findViewById(R.id.latestSearchHeading)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setBtnBack() {
        btnBack.setOnClickListener { this.finish() }
    }
    private fun setBtnClear() {
        clearBtn.setOnClickListener {
            inputEditText.setText(TEXT_DEF)
            searchedTracks.clear()
            tracksAdapter.notifyDataSetChanged()
            showMessage(ErrorMessageType.NO_MESSAGE)
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }
    }
    private fun setBtnReload() {
        reloadBtn.setOnClickListener {
            if (inputEditText.text.isNotEmpty()) {
                viewModel.searchDebounce(changedText = inputEditText.text.toString())
            }
        }
    }
    private fun setClearHistoryBtn() {
        clearHistoryBtn.setOnClickListener {
            savedTracks.clear()
            hintLatestSearch.visibility = View.GONE
            searchTracksAdapter.notifyDataSetChanged()
        }
    }
    private fun setButtons() {
        setBtnBack()
        setBtnClear()
        setBtnReload()
        setClearHistoryBtn()
    }

    private fun setTextWatcher() {
        textWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textValue = s.toString()
                clearBtn.visibility = clearButtonVisibility(s)
                if (s != null) {
                    hintLatestSearch.visibility = if (inputEditText.hasFocus() && s.isEmpty() && savedTracks.isNotEmpty()) {
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
        inputEditText.addTextChangedListener(textWatcher)
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
        hintLatestSearch.visibility = if (hasFocus && inputEditText.text.isEmpty() && searchTracksAdapter.tracks.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun createOnTrackClick(): OnTrackClickListener {
        val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)

        val trackListener = OnTrackClickListener { track: Track ->
            addSearchTrack(track)

            if (clickDebounce()) {
                playerIntent.putExtra(TRACK, track)

                startActivity(playerIntent)
            }
        }

        return trackListener
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true}, CLICK_DEBOUNCE_DELAY_MILLIS)
        }
        return current
    }

    private fun showMessage(type: ErrorMessageType) {
        when(type) {
            ErrorMessageType.SOMETHING_WENT_WRONG -> {
                phNothingFound.visibility = View.GONE
                phSomethingWentWrong.visibility = View.VISIBLE
            }
            ErrorMessageType.NOTHING_FOUND -> {
                phSomethingWentWrong.visibility = View.GONE
                phNothingFound.visibility = View.VISIBLE
            }
            ErrorMessageType.NO_MESSAGE -> {
                phSomethingWentWrong.visibility = View.GONE
                phNothingFound.visibility = View.GONE
            }
        }

        progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        rvTrackSearch.visibility = View.GONE
    }

    private fun showContent(foundTracks: List<Track>) {
        progressBar.visibility = View.GONE
        searchedTracks.clear()
        searchedTracks.addAll(foundTracks)
        rvTrackSearch.visibility = View.VISIBLE
        tracksAdapter.notifyDataSetChanged()
    }

    private fun addSearchedTracks(tracks: List<Track>) {
        savedTracks.addAll(tracks)
        searchTracksAdapter.notifyDataSetChanged()
    }

    private fun render(state: TracksState) {
        when(state) {
            is TracksState.SearchedContent -> showContent(state.searchedTracks)
            is TracksState.HistoryContent -> addSearchedTracks(state.savedTracks)
            is TracksState.Error -> showMessage(state.errorMessage)
            TracksState.Loading -> showLoading()
        }
    }

    companion object {
        private const val INPUT_TEXT = "INPUT_TEXT"
        private const val TEXT_DEF = ""
        private const val LATEST_SEARCH_TRACKS_SIZE = 10
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }

}