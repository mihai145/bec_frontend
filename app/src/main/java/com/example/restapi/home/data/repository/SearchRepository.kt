package com.example.restapi.home.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapi.home.data.model.request.ActorInfoModel
import com.example.restapi.home.data.model.request.MovieInfoModel
import com.example.restapi.home.data.model.request.SearchByActorNameModel
import com.example.restapi.home.data.model.response.MovieResponseModel
import com.example.restapi.home.data.model.request.SearchByMovieNameModel
import com.example.restapi.home.data.model.response.ActorInfoResponseModel
import com.example.restapi.home.data.model.response.ActorResponseModel
import com.example.restapi.home.data.model.response.MovieInfoResponseModel
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

    fun searchByMovieName(movieName : String):LiveData<MovieResponseModel>{
        val data = MutableLiveData<MovieResponseModel>()
        val movieNameModel = SearchByMovieNameModel(movieName)

        apiInterface?.searchByMovieName(movieNameModel)?.enqueue(object : Callback<MovieResponseModel>{
            override fun onFailure(call: Call<MovieResponseModel>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(call: Call<MovieResponseModel>, response: Response<MovieResponseModel>) {
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
    fun searchByActorName (actorName: String):LiveData<ActorResponseModel> {
        val data = MutableLiveData<ActorResponseModel>()
        val actorNameModel = SearchByActorNameModel(actorName)

        apiInterface?.searchByActorName(actorNameModel)?.enqueue(object : Callback<ActorResponseModel>{
            override fun onFailure(call: Call<ActorResponseModel>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(call: Call<ActorResponseModel>, response: Response<ActorResponseModel>) {
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
    fun movieInfo(movieId: Long): LiveData<MovieInfoResponseModel>{
        val data = MutableLiveData<MovieInfoResponseModel>()
        val requestModel = MovieInfoModel(movieId)
        Log.d("Repo", "movieInfo $movieId")
        apiInterface?.movieInfo(requestModel)?.enqueue(object : Callback<MovieInfoResponseModel>{
            override fun onFailure(call: Call<MovieInfoResponseModel>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(call: Call<MovieInfoResponseModel>, response: Response<MovieInfoResponseModel>) {
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
    fun actorInfo(actorId: Long): LiveData<ActorInfoResponseModel>{
        val data = MutableLiveData<ActorInfoResponseModel>()
        val requestModel = ActorInfoModel(actorId)
        Log.d("Repo", "actorInfo $actorId")
        apiInterface?.actorInfo(requestModel)?.enqueue(object : Callback<ActorInfoResponseModel>{
            override fun onFailure(call: Call<ActorInfoResponseModel>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(call: Call<ActorInfoResponseModel>, response: Response<ActorInfoResponseModel>) {
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
