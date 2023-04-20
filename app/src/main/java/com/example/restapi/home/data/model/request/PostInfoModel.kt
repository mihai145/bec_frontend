package com.example.restapi.home.data.model.request

class PostInfoModel {
    var postId: Int? = 0

    constructor(postId: Long) {
        this.postId = postId.toInt()
    }
}