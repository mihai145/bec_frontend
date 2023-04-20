package com.example.restapi.home.data.model.response

import com.example.restapi.home.data.model.PostModel

data class PostsResponseModel (
    var ok: Boolean,
    var posts: List<PostModel>
)