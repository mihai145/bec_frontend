package com.example.restapi.home.data.model.request

class CommentLikedModel {
    var commentId: Int? = 0
    var authorId: Int? = 0

    constructor(commentId: Long, authorId: Long) {
        this.commentId = commentId.toInt()
        this.authorId = authorId.toInt()
    }
}