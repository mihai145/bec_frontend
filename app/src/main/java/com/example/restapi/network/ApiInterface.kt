package com.example.restapi.network

import com.example.restapi.home.data.model.request.*
import com.example.restapi.home.data.model.response.*
import org.intellij.lang.annotations.Language
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @POST("search/movieName")
    fun searchByMovieName(@Body movieName: SearchByMovieNameModel): Call<MovieResponseModel>
    @POST("search/actorName")
    fun searchByActorName(@Body actorName: SearchByActorNameModel): Call<ActorResponseModel>
    @POST("search/movieId")
    fun movieInfo(@Body movieId : MovieInfoModel): Call <MovieInfoResponseModel>
    @POST("search/actorId")
    fun actorInfo(@Body actorId : ActorInfoModel): Call <ActorInfoResponseModel>
    @POST("/amIFollowing")
    fun userFollowInfo(@Header("Bearer") token : String, @Body follow : UserFollowModel): Call<UserFollowResponseModel>
    @POST("/follow")
    fun follow(@Header("Bearer") token : String, @Body follow : UserFollowModel): Call<SimpleResponseModel>
    @POST("/unfollow")
    fun unfollow(@Header("Bearer") token : String, @Body follow : UserFollowModel): Call<SimpleResponseModel>
    @POST("search/nickname")
    fun searchByUserName(@Body nickname: SearchByUsernameModel): Call<UserSearchModel>
}
