package com.example.restapi.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapi.home.data.model.response.*
import com.example.restapi.home.data.repository.SearchRepository

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private var searchRepository: SearchRepository? = null
    var searchedMoviesLiveData: LiveData<MovieResponseModel>? = null
    var searchedActorsLiveData: LiveData<ActorResponseModel>? = null
    var searchedUsersLiveData: LiveData<UserSearchModel>? = null
    var movieInfoLiveData: LiveData<MovieInfoResponseModel>? = null
    var actorInfoLiveData: LiveData<ActorInfoResponseModel>? = null
    var userFollowLiveData: LiveData<UserFollowResponseModel>? = null
    var postInfoLiveData: LiveData<PostInfoResponseModel>? = null
    var postsLiveData: LiveData<PostsResponseModel>? = null
    var commentsLiveData: LiveData<CommentsResponseModel>? = null
    var userPostsLiveData: LiveData<PostsResponseModel>? = null
    var leaderboardLiveData: LiveData<LeaderboardResponseModel>? = null
    var likes: LiveData<Int>? = null
    var wasLiked: LiveData<Int>? = null

    init {
        searchRepository = SearchRepository()
        searchedMoviesLiveData = MutableLiveData()
        searchedActorsLiveData = MutableLiveData()
        movieInfoLiveData = MutableLiveData()
        postsLiveData = MutableLiveData()
        userPostsLiveData = MutableLiveData()
    }

    fun searchUser(nickname: String) {
        searchedUsersLiveData = searchRepository?.searchByUser(nickname)
    }

    fun searchMoviesByName(movieName: String) {
        searchedMoviesLiveData = searchRepository?.searchByMovieName(movieName)
    }

    fun searchActorByName(actorName: String) {
        searchedActorsLiveData = searchRepository?.searchByActorName(actorName)
    }

    fun movieInfo(movieId: Long) {
        movieInfoLiveData = searchRepository?.movieInfo(movieId)
    }

    fun actorInfo(actorId: Long) {
        actorInfoLiveData = searchRepository?.actorInfo(actorId)
    }

    fun userFollowInfo(followerId: Long, followeeId: Long) {
        userFollowLiveData = searchRepository?.userFollowInfo(followerId, followeeId)
    }

    fun postInfo(postId: Long) {
        postInfoLiveData = searchRepository?.postInfo(postId)
    }

    fun getPosts(userId: Long) {
        postsLiveData = searchRepository?.getPosts(userId)
    }

    fun getUserPosts(userId: Long) {
        userPostsLiveData = searchRepository?.getUserPosts(userId)
    }

    fun getComments(postId: Long) {
        commentsLiveData = searchRepository?.getComments(postId)
    }

    fun getLeaderboard() {
        leaderboardLiveData = searchRepository?.getLeaderboard()
    }

    fun wasLikedPost(postId: Long, userId: Long) {
        wasLiked = searchRepository?.wasLikedPost(postId, userId)
    }

    fun getLikesPost(postId: Long) {
        likes = searchRepository?.getLikesPost(postId)
    }
}