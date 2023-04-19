package com.example.restapi.home.data.model.response

import com.example.restapi.home.data.model.PostModel

data class PostInfoResponseModel(
    val ok: Boolean,
    val post: PostModel
)