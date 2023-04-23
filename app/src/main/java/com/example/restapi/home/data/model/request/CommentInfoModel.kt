package com.example.restapi.home.data.model.request

class CommentInfoModel {
    var commentId: Int? = 0

    constructor(commentId: Long) {
        this.commentId = commentId.toInt()
    }
}