package com.example.movieexplorerapp

import com.google.gson.annotations.SerializedName

data class Movie(
    val title: String,
    val description: String,
    val director: String,

    @SerializedName("release_date")
    val releaseDate: String
)