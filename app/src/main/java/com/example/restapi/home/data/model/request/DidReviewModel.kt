package com.example.restapi.home.data.model.request

class DidReviewModel {
    var authorId: Int? = 0
    var movieId: Int? = 0

    constructor(authorId: Long, movieId: Long) {
        this.authorId = authorId.toInt()
        this.movieId = movieId.toInt()
    }
}