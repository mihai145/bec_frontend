package com.example.restapi.home.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapi.home.data.model.request.SearchByGenreIdModel
import com.example.restapi.home.data.model.response.GenreResponseModel
import com.example.restapi.home.data.model.response.TrendingResponseModel
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrendingRepository {
    private var apiInterface: ApiInterface? = null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    fun getGenres(): LiveData<GenreResponseModel> {
        val data = MutableLiveData<GenreResponseModel>()
        apiInterface?.getGenres()?.enqueue(object : Callback<GenreResponseModel> {
            override fun onFailure(call: Call<GenreResponseModel>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(
                call: Call<GenreResponseModel>,
                response: Response<GenreResponseModel>
            ) {
                val res = response.body()
                if (response.code() == 202 && res != null) {
                    data.value = res
                } else {
                    data.value = null
                }
            }
        })
        return data
    }

    fun getTrending(genreId: Int): LiveData<TrendingResponseModel> {
        val data = MutableLiveData<TrendingResponseModel>()
        val requestModel = SearchByGenreIdModel(genreId)
        apiInterface?.getTrending(requestModel)?.enqueue(object : Callback<TrendingResponseModel> {
            override fun onFailure(call: Call<TrendingResponseModel>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(
                call: Call<TrendingResponseModel>,
                response: Response<TrendingResponseModel>
            ) {
                val res = response.body()
                if (response.code() == 202 && res != null) {
                    data.value = res
                } else {
                    data.value = null
                }
            }
        })
        return data
    }
}