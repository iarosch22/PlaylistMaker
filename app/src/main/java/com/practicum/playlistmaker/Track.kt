package com.practicum.playlistmaker

import android.os.Parcel
import android.os.Parcelable

data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
    ): Parcelable {
    constructor(parcel: Parcel) : this(
        trackId = parcel.readString()!!,
        trackName = parcel.readString()!!,
        artistName = parcel.readString()!!,
        trackTimeMillis = parcel.readString()!!,
        artworkUrl100 = parcel.readString()!!,
        collectionName = parcel.readString(),
        releaseDate = parcel.readString()!!,
        primaryGenreName = parcel.readString()!!,
        country = parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(trackId)
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeString(trackTimeMillis)
        parcel.writeString(artworkUrl100)
        parcel.writeString(collectionName)
        parcel.writeString(releaseDate)
        parcel.writeString(primaryGenreName)
        parcel.writeString(country)
    }

    override fun describeContents(): Int {
        return 0
    }


    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")


    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}