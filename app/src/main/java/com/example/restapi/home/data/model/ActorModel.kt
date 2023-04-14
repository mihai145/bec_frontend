package com.example.restapi.home.data.model

import com.google.gson.annotations.SerializedName

data class ActorModel (
    val adult: Boolean,
    val biography: String,
    val birthday: String,
    val gender: Long,
    val id: Long,

    @SerializedName("also_known_as")
    val alsoKnownAs: List<Any?>,

    @SerializedName("known_for")
    val knownFor: List<MovieModel>,

    @SerializedName("known_for_department")
    val knownForDepartment: String,

    @SerializedName("place_of_birth")
    val placeOfBirth: String,

    val name: String,
    val popularity: Double,

    @SerializedName("original_name")
    val originalName: String,

    @SerializedName("profile_path")
    val profilePath: String? = null
)