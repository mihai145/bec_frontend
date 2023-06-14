package com.example.restapi.home.data.model.response

data class GptResponseModel (
    val ok: Boolean,
    val results: List<String>
)