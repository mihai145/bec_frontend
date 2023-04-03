package com.example.restapi.network

import com.example.restapi.home.data.model.request.SearchByActorNameModel
import com.example.restapi.home.data.model.response.MovieSearchModel
import com.example.restapi.home.data.model.request.SearchByMovieNameModel
import com.example.restapi.home.data.model.response.ActorSearchModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("search/movieName")
    fun searchByMovieName(@Body movieName: SearchByMovieNameModel): Call<MovieSearchModel>
    @POST("search/actorName")
    fun searchByActorName(@Body actorName: SearchByActorNameModel): Call<ActorSearchModel>
}
