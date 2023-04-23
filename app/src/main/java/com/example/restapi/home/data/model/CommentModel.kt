package com.example.restapi.home.data.model

data class CommentModel(
    val id: Long,

    val authorId: Long,
    val authorNickname: String,
    val content: String,

    val postId: Long,
)