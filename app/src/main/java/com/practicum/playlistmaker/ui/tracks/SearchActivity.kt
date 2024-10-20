package com.practicum.playlistmaker.ui.tracks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.PlayerActivity

const val TRACK = "TRACK"

class SearchActivity : AppCompatActivity() {

    private var textValue = TEXT_DEF

    private var isClickAllowed = true

    private val tracks = ArrayList<Track>()
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var searchTracksAdapter: TracksAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }

    private lateinit var trackInteractor: TracksInteractor

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

        trackInteractor = Creator.provideTracksInteractor(this)

        inputMethodManager = (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)!!

        setViews()

        setAdapters()

        setButtons()

        setTextWatcher()

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                if (inputEditText.text.isNotEmpty()) {
                    performSearch()
                }
                true
            }
            false
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus -> showLatestSearch(hasFocus) }
    }

    override fun onStop() {
        super.onStop()

        trackInteractor.saveSearchedTracks(searchTracksAdapter.tracks)
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

        tracksAdapter.tracks = tracks
        rvTrackSearch.adapter = tracksAdapter

        searchTracksAdapter.tracks = trackInteractor.getSearchedTracks()
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
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            showMessage(NO_MESSAGE)
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }
    }
    private fun setBtnReload() {
        reloadBtn.setOnClickListener {
            if (inputEditText.text.isNotEmpty()) {
                performSearch()
            }
        }
    }
    private fun setClearHistoryBtn() {
        clearHistoryBtn.setOnClickListener {
            trackInteractor.clearHistory()

            searchTracksAdapter.tracks.clear()
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
        val textWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textValue = s.toString()
                clearBtn.visibility = clearButtonVisibility(s)
                hintLatestSearch.visibility = if (inputEditText.hasFocus() && s?.isEmpty() == true && searchTracksAdapter.tracks.isNotEmpty()) {
                    View.VISIBLE
                } else {
                    searchDebounce()
                    View.GONE
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(textWatcher)
    }

    private fun showMessage(type: String) {
        when(type) {
            SOMETHING_WENT_WRONG -> {
                phNothingFound.visibility = View.GONE
                phSomethingWentWrong.visibility = View.VISIBLE
            }
            NOTHING_FOUND -> {
                phSomethingWentWrong.visibility = View.GONE
                phNothingFound.visibility = View.VISIBLE
            }
            NO_MESSAGE -> {
                phSomethingWentWrong.visibility = View.GONE
                phNothingFound.visibility = View.GONE
            }
        }
    }

    private fun performSearch() {
        val query = inputEditText.text.toString()

        progressBar.visibility = View.VISIBLE
        phNothingFound.visibility = View.GONE
        phSomethingWentWrong.visibility = View.GONE
        rvTrackSearch.visibility = View.GONE

        trackInteractor.searchTracks(query, object : TracksInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>) {
                handler.post{
                    progressBar.visibility = View.GONE
                    if (foundTracks.isNotEmpty()) {
                        tracks.clear()
                        rvTrackSearch.visibility = View.VISIBLE
                        tracks.addAll(foundTracks)
                        tracksAdapter.notifyDataSetChanged()
                    } else {
                        showMessage(NOTHING_FOUND)
                    }
                }
            }
        })
    }

    private fun addSearchTrack(track: Track) {
        val duplicateTrackIndex = searchTracksAdapter.tracks.indexOfFirst { it.trackId == track.trackId }

        if (duplicateTrackIndex !=  -1) {
            deleteSearchTrack(duplicateTrackIndex)
        }

        if (searchTracksAdapter.itemCount == LATEST_SEARCH_TRACKS_SIZE) {
            val positionToRemove = LATEST_SEARCH_TRACKS_SIZE - 1

            deleteSearchTrack(positionToRemove)
        }

        searchTracksAdapter.tracks.add(0, track)
        searchTracksAdapter.notifyDataSetChanged()
    }

    private fun deleteSearchTrack(position: Int) {
        searchTracksAdapter.tracks.removeAt(position)
        searchTracksAdapter.notifyItemRemoved(position)
        searchTracksAdapter.notifyItemRangeChanged(position, searchTracksAdapter.tracks.size)
    }

    private fun showLatestSearch(hasFocus: Boolean) { hintLatestSearch.visibility = if (
        hasFocus && inputEditText.text.isEmpty() && searchTracksAdapter.tracks.isNotEmpty()
        ) View.VISIBLE else  View.GONE }

    private fun createOnTrackClick(): OnTrackClickListener {
        val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)

        val trackListener = OnTrackClickListener { track: Track ->
            trackInteractor.addTrackToHistory(track)
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
            handler.postDelayed({ isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        private const val INPUT_TEXT = "INPUT_TEXT"
        private const val TEXT_DEF = ""
        private const val LATEST_SEARCH_TRACKS_SIZE = 10
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        private const val SOMETHING_WENT_WRONG = "SOMETHING_WENT_WRONG"
        private const val NOTHING_FOUND = "NOTHING_FOUND"
        private const val NO_MESSAGE = "NO_MESSAGE"
    }

}