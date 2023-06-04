package com.example.restapi.home.data.model.request

class GptRequestModel {
    var preferences: List<String>? = null

    constructor(preferences: List<String>)
    {
        this.preferences = preferences
    }
}