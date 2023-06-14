package com.example.restapi.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restapi.home.data.model.response.GptResponseModel
import com.example.restapi.home.data.repository.GptRepository


class GptViewModel (application: Application) : AndroidViewModel(application) {
    private var gptRepository: GptRepository? = null
    var preferencesLiveData: LiveData<GptResponseModel>? = null

    init {
        gptRepository = GptRepository()
        preferencesLiveData = MutableLiveData()
    }
    fun askGpt(preferences: List<String>) {
        preferencesLiveData = gptRepository?.askGpt(preferences)
    }

}
