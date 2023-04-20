package com.example.restapi.home.data.model.request

class EditPostModel {
    var postId: Int? = 0
    var title: String? = ""
    var content: String? = ""

    constructor(postId: Int, title: String, content: String) {
        this.postId = postId
        this.title = title
        this.content = content
    }
}