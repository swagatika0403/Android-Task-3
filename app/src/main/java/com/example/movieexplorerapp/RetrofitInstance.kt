package com.example.movieexplorerapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: MovieApiService by lazy {

        Retrofit.Builder()
            .baseUrl("https://ghibliapi.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)
    }
}