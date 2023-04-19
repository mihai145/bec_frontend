package com.example.restapi.network

import com.example.restapi.home.data.model.request.*
import com.example.restapi.home.data.model.response.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @POST("search/movieName")
    fun searchByMovieName(@Body movieName: SearchByMovieNameModel): Call<MovieResponseModel>

    @POST("search/actorName")
    fun searchByActorName(@Body actorName: SearchByActorNameModel): Call<ActorResponseModel>

    @POST("search/movieId")
    fun movieInfo(@Body movieId: MovieInfoModel): Call<MovieInfoResponseModel>

    @POST("search/actorId")
    fun actorInfo(@Body actorId: ActorInfoModel): Call<ActorInfoResponseModel>

    @POST("/amIFollowing")
    fun userFollowInfo(
        @Header("Bearer") token: String,
        @Body follow: UserFollowModel
    ): Call<UserFollowResponseModel>

    @POST("/follow")
    fun follow(
        @Header("Bearer") token: String,
        @Body follow: UserFollowModel
    ): Call<SimpleResponseModel>

    @POST("/unfollow")
    fun unfollow(
        @Header("Bearer") token: String,
        @Body follow: UserFollowModel
    ): Call<SimpleResponseModel>

    @POST("search/nickname")
    fun searchByUserName(@Body nickname: SearchByUsernameModel): Call<UserSearchModel>

    @POST("didIReview")
    fun didIReview(
        @Header("Bearer") token: String,
        @Body didReview: DidReviewModel
    ): Call<DidReviewResponseModel>

    @POST("getPost")
    fun getPost(
        @Header("Bearer") token: String,
        @Body postInfo: PostInfoModel
    ): Call<PostInfoResponseModel>

    @POST("post")
    fun submitPost(
        @Header("Bearer") token: String,
        @Body submitPost: SubmitPostModel
    ): Call<SimpleResponseModel>

    @POST("editPost")
    fun editPost(
        @Header("Bearer") token: String,
        @Body submitPost: EditPostModel
    ): Call<SimpleResponseModel>

    @POST("deletePost")
    fun deletePost(
        @Header("Bearer") token: String,
        @Body postInfo: PostInfoModel
    ): Call<SimpleResponseModel>

    @GET("posts")
    fun posts(@Header("Bearer") token: String): Call<PostsResponseModel>

    @GET("genres")
    fun getGenres(): Call<GenreResponseModel>

    @POST("trending")
    fun getTrending(@Body genreId: SearchByGenreIdModel): Call<TrendingResponseModel>
}
