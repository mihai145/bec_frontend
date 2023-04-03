package com.example.restapi.home.data.model

import com.beust.klaxon.*

private val klaxon = Klaxon()

data class MovieModel (
    val adult: Boolean,

    @Json(name = "backdrop_path")
    val backdropPath: String,

    @Json(name = "genre_ids")
    val genreIDS: List<Long>,

    val id: Long,
    val overview: String,
    val popularity: Double,

    @Json(name = "poster_path")
    val posterPath: String,

    @Json(name = "release_date")
    val releaseDate: String,

    val title: String
) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<MovieModel>(json)
    }
}
