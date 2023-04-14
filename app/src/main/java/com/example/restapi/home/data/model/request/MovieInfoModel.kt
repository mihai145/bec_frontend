package com.example.restapi.home.data.model.request

class MovieInfoModel {
    var movieId:Int? = 0
    constructor(movieId : Long)
    {
        this.movieId = movieId.toInt()
    }
}