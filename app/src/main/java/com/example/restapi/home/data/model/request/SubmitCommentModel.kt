package com.example.restapi.home.data.model.request

class SubmitCommentModel {
    var authorId: Int? = 0
    var content: String? = ""
    var postId: Long? = 0

    constructor(authorId: Int, content: String, postId: Long?) {
        this.authorId = authorId
        this.content = content
        this.postId = postId
    }
}