package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var textValue = TEXT_DEF

    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = createRetrofit()
    private val itunesService = retrofit.create(ItunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchTrackAdapter: TrackAdapter

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var listener: OnSharedPreferenceChangeListener
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        trackAdapter = TrackAdapter(this)
        searchTrackAdapter = TrackAdapter(this)

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

        trackAdapter.tracks = tracks
        rvTrackSearch.adapter = trackAdapter

        searchTrackAdapter.tracks = searchHistory.readTracksFromSearchHistory()
        rvLatestTrack.adapter = searchTrackAdapter


        setButtons()

        setTextWatcher()

        listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == APP_NEW_TRACK_KEY) {
                val track = sharedPreferences?.getString(APP_NEW_TRACK_KEY, null)
                if (track != null) {
                    searchTrackAdapter.tracks.add(0, searchHistory.createTrackFromJson(track))
                    searchTrackAdapter.notifyDataSetChanged()
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                if (inputEditText.text.isNotEmpty()) {
                    performSearch(inputEditText.text.toString())
                }
                true
            }
            false
        }

        inputEditText.setOnFocusChangeListener { v, hasFocus ->
            hintLatestSearch.visibility = if (hasFocus && inputEditText.text.isEmpty()) View.VISIBLE else  View.GONE
        }
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
    private fun setButtons() {
        setBtnBack()
        setBtnClear()
        setBtnReload()
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

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val TEXT_DEF = ""
    }

}