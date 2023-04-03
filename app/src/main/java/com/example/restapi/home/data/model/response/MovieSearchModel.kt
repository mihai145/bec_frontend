package com.example.restapi.home.data.model.response

import com.beust.klaxon.*
import com.example.restapi.home.data.model.MovieModel

private val klaxon = Klaxon()

data class MovieSearchModel (
    val ok: Boolean,
    val results: List<MovieModel>
) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<MovieSearchModel>(json)
    }
}
