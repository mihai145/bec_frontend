package com.example.restapi.home.data.model.request

class PostLikedModel {
    var postId: Int? = 0
    var authorId: Int? = 0

    constructor(postId: Long, authorId: Long) {
        this.postId = postId.toInt()
        this.authorId = authorId.toInt()
    }
}