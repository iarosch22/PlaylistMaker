package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val APP_SEARCH_HISTORY = "app_search_history"
const val APP_SEARCH_TRACKS_KEY = "app_search_tracks_key"
const val APP_NEW_TRACK_KEY = "app_new_track_key"

class SearchActivity : AppCompatActivity() {

    private var textValue = TEXT_DEF

    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = createRetrofit()
    private val itunesService = retrofit.create(ItunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchTrackAdapter: TrackAdapter

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferenceChangeListener: OnSharedPreferenceChangeListener
    private lateinit var searchHistory: SearchHistory

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        trackAdapter = TrackAdapter(createOnTrackClick())
        searchTrackAdapter = TrackAdapter(createOnTrackClick())

        inputMethodManager = (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)!!

        sharedPreferences = getSharedPreferences(APP_SEARCH_HISTORY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

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

        trackAdapter.tracks = tracks
        rvTrackSearch.adapter = trackAdapter

        searchTrackAdapter.tracks = searchHistory.readTracksFromSearchHistory()
        rvLatestTrack.adapter = searchTrackAdapter


        setButtons()

        setTextWatcher()

        sharedPreferenceChangeListener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == APP_NEW_TRACK_KEY) {
                val trackJson = sharedPreferences?.getString(APP_NEW_TRACK_KEY, null)
                if (trackJson != null) {
                    addSearchTrack(searchHistory.createTrackFromJson(trackJson))
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                if (inputEditText.text.isNotEmpty()) {
                    performSearch(inputEditText.text.toString())
                }
                true
            }
            false
        }

        inputEditText.setOnFocusChangeListener { v, hasFocus -> showLatestSearch(hasFocus) }
    }

    override fun onStop() {
        super.onStop()

        searchHistory.saveToSearchHistory(searchTrackAdapter.tracks)
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

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun setBtnBack() {
        btnBack.setOnClickListener { this.finish() }
    }
    private fun setBtnClear() {
        clearBtn.setOnClickListener {
            inputEditText.setText(TEXT_DEF)
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            showMessage(MessageType.NO_MESSAGE)
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }
    }
    private fun setBtnReload() {
        reloadBtn.setOnClickListener {
            if (inputEditText.text.isNotEmpty()) {
                performSearch(inputEditText.text.toString())
            }
        }
    }
    private fun setClearHistoryBtn() {
        clearHistoryBtn.setOnClickListener {
            searchHistory.clearHistory()

            searchTrackAdapter.tracks.clear()
            hintLatestSearch.visibility = View.GONE
            searchTrackAdapter.notifyDataSetChanged()
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

                hintLatestSearch.visibility = if (inputEditText.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(textWatcher)
    }

    private fun showMessage(type: MessageType) {
        when(type) {
            MessageType.SOMETHING_WENT_WRONG -> {
                phNothingFound.visibility = View.GONE
                phSomethingWentWrong.visibility = View.VISIBLE
            }
            MessageType.NOTHING_FOUND -> {
                phSomethingWentWrong.visibility = View.GONE
                phNothingFound.visibility = View.VISIBLE
            }
            MessageType.NO_MESSAGE -> {
                phSomethingWentWrong.visibility = View.GONE
                phNothingFound.visibility = View.GONE
            }
        }
    }

    private fun performSearch(inputText: String) {
        itunesService.search(inputText).enqueue(object: Callback<TrackResponce> {
            override fun onResponse(
                call: Call<TrackResponce>,
                response: Response<TrackResponce>
            ) {
                if (response.code() == 200) {
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        trackAdapter.notifyDataSetChanged()
                    }
                    if (tracks.isEmpty()) {
                        showMessage(MessageType.NOTHING_FOUND)
                    } else {
                        showMessage(MessageType.NO_MESSAGE)
                    }
                } else {
                    showMessage(MessageType.SOMETHING_WENT_WRONG)
                }
            }

            override fun onFailure(call: Call<TrackResponce>, t: Throwable) {
                showMessage(MessageType.SOMETHING_WENT_WRONG)
            }
        })
    }

    private fun addSearchTrack(track: Track) {
        val duplicateTrackIndex = searchTrackAdapter.tracks.indexOfFirst { it.trackId == track.trackId }

        if (duplicateTrackIndex !=  -1) {
            deleteSearchTrack(duplicateTrackIndex)
        }

        if (searchTrackAdapter.itemCount == LATEST_SEARCH_TRACKS_SIZE) {
            val positionToRemove = LATEST_SEARCH_TRACKS_SIZE - 1

            deleteSearchTrack(positionToRemove)
        }

        searchTrackAdapter.tracks.add(0, track)
        searchTrackAdapter.notifyDataSetChanged()
    }

    private fun deleteSearchTrack(position: Int) {
        searchTrackAdapter.tracks.removeAt(position)
        searchTrackAdapter.notifyItemRemoved(position)
        searchTrackAdapter.notifyItemRangeChanged(position, searchTrackAdapter.tracks.size)
    }

    private fun showLatestSearch(hasFocus: Boolean) { hintLatestSearch.visibility = if (hasFocus && inputEditText.text.isEmpty()) View.VISIBLE else  View.GONE }

    private fun createOnTrackClick(): OnTrackClickListener {
        val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)

        val trackListener = OnTrackClickListener { track: Track ->
            sharedPreferences.edit()
                .putString(APP_NEW_TRACK_KEY, Gson().toJson(track))
                .apply()

            playerIntent.putExtra("TRACK", track)

            startActivity(playerIntent)
        }


        return trackListener
    }

    companion object {
        private const val INPUT_TEXT = "INPUT_TEXT"
        private const val TEXT_DEF = ""
        private const val LATEST_SEARCH_TRACKS_SIZE = 10
    }

}