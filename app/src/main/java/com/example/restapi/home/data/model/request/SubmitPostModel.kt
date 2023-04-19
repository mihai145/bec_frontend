package com.example.restapi.home.data.model.request

class SubmitPostModel {
    var authorId: Int? = 0
    var title: String? = ""
    var content: String? = ""
    var movieId: Long? = 0
    var movieName: String? = ""

    constructor(authorId: Int, title: String, content: String, movieId: Long?, movieName: String?) {
        this.authorId = authorId
        this.title = title
        this.content = content
        this.movieId = movieId
        this.movieName = movieName
    }
}