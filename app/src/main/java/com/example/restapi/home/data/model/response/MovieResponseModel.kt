package com.example.restapi.home.data.model.response

import com.example.restapi.home.data.model.MovieModel


data class MovieResponseModel (
    val ok: Boolean,
    val results: List<MovieModel>
)
