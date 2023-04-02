package com.example.restapi.home.data.model.response

import com.beust.klaxon.Klaxon
import com.example.restapi.home.data.model.ActorModel

private val klaxon = Klaxon()

data class ActorSearchModel (
    val ok: Boolean,
    val results: List<ActorModel>
) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<ActorSearchModel>(json)
    }
}