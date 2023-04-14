package com.example.restapi.home.data.model

import com.google.gson.annotations.SerializedName

data class ActorModel (
    val adult: Boolean,
    val gender: Long,
    val id: Long,

    @SerializedName("known_for")
    val knownFor: List<MovieModel>,

    @SerializedName("known_for_department")
    val knownForDepartment: String,

    val name: String,

    @SerializedName("original_name")
    val originalName: String,

    @SerializedName("profile_path")
    val profilePath: Any? = null
)