package com.example.restapi.home.data.model.response

import com.example.restapi.home.data.model.ActorModel
data class ActorResponseModel (
    val ok: Boolean,
    val results: List<ActorModel>
)