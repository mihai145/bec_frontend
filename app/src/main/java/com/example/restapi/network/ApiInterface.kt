package com.example.restapi.network

import com.example.restapi.home.data.model.request.MovieInfoModel
import com.example.restapi.home.data.model.request.SearchByActorNameModel
import com.example.restapi.home.data.model.response.MovieResponseModel
import com.example.restapi.home.data.model.request.SearchByMovieNameModel
import com.example.restapi.home.data.model.response.ActorResponseModel
import com.example.restapi.home.data.model.response.MovieInfoResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("search/movieName")
    fun searchByMovieName(@Body movieName: SearchByMovieNameModel): Call<MovieResponseModel>
    @POST("search/actorName")
    fun searchByActorName(@Body actorName: SearchByActorNameModel): Call<ActorResponseModel>
    @POST("search/movieId")
    fun movieInfo(@Body movieId : MovieInfoModel): Call <MovieInfoResponseModel>
}
