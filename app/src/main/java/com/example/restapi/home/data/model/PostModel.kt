package com.example.restapi.home.data.model

data class PostModel(
    val id: Long,

    val authorId: Long,
    val authorNickname: String,

    val title: String,
    val content: String,

    val movieId: Long? = null,
    val movieName: String? = null,
)