package com.example.movieexplorerapp

import retrofit2.http.GET

interface MovieApiService {

    @GET("films")
    suspend fun getMovies(): List<Movie>
}