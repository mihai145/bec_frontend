package com.example.restapi.home.data.model.response

import com.example.restapi.home.data.model.CommentModel

data class CommentsResponseModel (
    var ok: Boolean,
    var comments: List<CommentModel>
)