package com.example.restapi.home.data.model.response

import com.example.restapi.home.data.model.ActorModel

data class ActorInfoResponseModel (
    val ok: Boolean,
    val result: ActorModel
)