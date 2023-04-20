package com.example.restapi.home.data.model.response

data class DidReviewResponseModel(
    val ok: Boolean,
    val reviewed: Boolean,
    val postId: Int
)