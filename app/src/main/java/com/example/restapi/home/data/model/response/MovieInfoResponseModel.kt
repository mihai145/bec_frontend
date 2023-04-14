package com.example.restapi.home.data.model.response

import com.example.restapi.home.data.model.MovieModel

data class MovieInfoResponseModel (
    val ok: Boolean,
    val result: MovieModel
)