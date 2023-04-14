package com.example.restapi.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapi.home.data.model.response.ActorResponseModel
import com.example.restapi.home.data.model.response.MovieInfoResponseModel
import com.example.restapi.home.data.model.response.MovieResponseModel
import com.example.restapi.home.data.repository.SearchRepository

class SearchViewModel(application: Application) : AndroidViewModel(application){

    private var searchRepository: SearchRepository?=null
    var searchedMoviesLiveData: LiveData<MovieResponseModel>?=null
    var searchedActorsLiveData: LiveData<ActorResponseModel>?=null
    var movieInfoLiveData: LiveData<MovieInfoResponseModel>?=null;
    init{
        searchRepository = SearchRepository()
        searchedMoviesLiveData = MutableLiveData()
        searchedActorsLiveData = MutableLiveData()
        movieInfoLiveData = MutableLiveData()
    }

    fun searchMoviesByName(movieName: String){
        searchedMoviesLiveData = searchRepository?.searchByMovieName(movieName)
    }
    fun searchActorByName(actorName: String){
        searchedActorsLiveData = searchRepository?.searchByActorName(actorName)
    }
    fun movieInfo(movieId: Long){
        movieInfoLiveData = searchRepository?.movieInfo(movieId)
    }
}