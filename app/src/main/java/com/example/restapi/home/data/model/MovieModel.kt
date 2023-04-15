package com.example.restapi.home.data.model

import com.google.gson.annotations.SerializedName
data class MovieModel (
    val adult: Boolean,

    @SerializedName("backdrop_path")
    val backdropPath: String,

    val genres: List<GenreModel>,
    val id: Long,

    @SerializedName("imdb_id")
    val imdbID: String,

    val overview: String,
    val popularity: Double,

    @SerializedName("poster_path")
    val posterPath: String,

    @SerializedName("release_date")
    val releaseDate: String,

    val revenue: Long,
    val runtime: Long,
    val status: String,
    val tagline: String,
    val title: String
)
