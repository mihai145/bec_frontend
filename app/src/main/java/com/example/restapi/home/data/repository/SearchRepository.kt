package com.example.restapi.home.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapi.home.data.model.response.MovieSearchModel
import com.example.restapi.home.data.model.request.SearchByMovieNameModel
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class SearchRepository {
    private var apiInterface:ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    fun searchByMovieName(movieName : String):LiveData<MovieSearchModel>{
        val data = MutableLiveData<MovieSearchModel>()
        val movieNameModel = SearchByMovieNameModel(movieName)

        apiInterface?.searchByMovieName(movieNameModel)?.enqueue(object : Callback<MovieSearchModel>{
            override fun onFailure(call: Call<MovieSearchModel>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(call: Call<MovieSearchModel>, response: Response<MovieSearchModel>) {
                val res = response.body()
                if (response.code() == 202 && res!=null){
                    data.value = res
                }else{
                    data.value = null
                }
            }
        })
        return data
    }
}
