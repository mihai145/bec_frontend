package com.example.restapi.home.data.model.response

import com.example.restapi.home.data.model.CommentModel

data class CommentInfoResponseModel(
    val ok: Boolean,
    val comment: CommentModel
)