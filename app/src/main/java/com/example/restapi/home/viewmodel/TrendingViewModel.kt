package com.example.restapi.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapi.home.data.model.response.*
import com.example.restapi.home.data.repository.TrendingRepository

class TrendingViewModel (application: Application) : AndroidViewModel(application) {
    private var trendingRepository: TrendingRepository? = null
    var genresLiveData: LiveData<GenreResponseModel>? = null
    var trendingMoviesLiveData: LiveData<TrendingResponseModel>? = null
    init {
        trendingRepository = TrendingRepository()
        trendingMoviesLiveData = MutableLiveData()
        genresLiveData = MutableLiveData()
    }
    fun getTrending(genreId : Int) {
        trendingMoviesLiveData = trendingRepository?.getTrending(genreId)
    }

    fun getGenres() {
        genresLiveData = trendingRepository?.getGenres()
    }
}
