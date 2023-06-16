package com.example.restapi.home.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bec_client.MainActivity
import com.example.restapi.home.data.model.request.*
import com.example.restapi.home.data.model.response.*
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GptRepository {
    private var apiInterface: ApiInterface? = null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    fun askGpt(preferences: List<String>): LiveData<GptResponseModel> {
        val data = MutableLiveData<GptResponseModel>()
        val request = GptRequestModel(preferences)

        apiInterface?.askGPT(request)
            ?.enqueue(object : Callback<GptResponseModel> {
                override fun onFailure(call: Call<GptResponseModel>, t: Throwable) {
                    data.value = null
                }

                override fun onResponse(
                    call: Call<GptResponseModel>,
                    response: Response<GptResponseModel>
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
