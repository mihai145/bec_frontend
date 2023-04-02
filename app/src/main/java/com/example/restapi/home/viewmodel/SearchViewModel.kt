package com.example.restapi.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapi.home.data.model.response.MovieSearchModel
import com.example.restapi.home.data.repository.SearchRepository

class SearchViewModel(application: Application) : AndroidViewModel(application){

    private var searchRepository: SearchRepository?=null
    var searchedMoviesLiveData: LiveData<MovieSearchModel>?=null

    init{
        searchRepository = SearchRepository()
        searchedMoviesLiveData = MutableLiveData()
    }

    fun searchMoviesByName(movieName: String){
        searchedMoviesLiveData = searchRepository?.searchByMovieName(movieName)
    }
}