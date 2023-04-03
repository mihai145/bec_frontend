package com.example.restapi.home.data.model

import com.beust.klaxon.*

private val klaxon = Klaxon()

data class ActorModel (
    val adult: Boolean,
    val gender: Long,
    val id: Long,

    @Json(name = "known_for")
    val knownFor: List<MovieModel>,

    @Json(name = "known_for_department")
    val knownForDepartment: String,

    val name: String,

    @Json(name = "original_name")
    val originalName: String,

    @Json(name = "profile_path")
    val profilePath: Any? = null
) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<MovieModel>(json)
    }
}