package com.example.restapi.home.viewmodel

import android.app.Application
import android.provider.ContactsContract.CommonDataKinds.Nickname
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapi.home.data.model.response.ActorSearchModel
import com.example.restapi.home.data.model.response.MovieSearchModel
import com.example.restapi.home.data.model.response.UserSearchModel
import com.example.restapi.home.data.repository.SearchRepository

class SearchViewModel(application: Application) : AndroidViewModel(application){

    private var searchRepository: SearchRepository?=null
    var searchedMoviesLiveData: LiveData<MovieSearchModel>?=null
    var searchedActorsLiveData: LiveData<ActorSearchModel>?=null
    var searchedUsersLiveData: LiveData<UserSearchModel>?=null
    init{
        searchRepository = SearchRepository()
        searchedMoviesLiveData = MutableLiveData()
        searchedActorsLiveData = MutableLiveData()
        searchedUsersLiveData = MutableLiveData()
    }

    fun searchUser(nickname: String) {
        searchedUsersLiveData = searchRepository?.searchByUser(nickname)
    }
    fun searchMoviesByName(movieName: String){
        searchedMoviesLiveData = searchRepository?.searchByMovieName(movieName)
    }
    fun searchActorByName(actorName: String){
        searchedActorsLiveData = searchRepository?.searchByActorName(actorName)
    }
}