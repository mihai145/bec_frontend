package com.example.restapi.home.data.model.request

class EditCommentModel {
    var commentId: Int? = 0
    var content: String? = ""

    constructor(commentId: Int, content: String) {
        this.commentId = commentId
        this.content = content
    }
}