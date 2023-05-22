package com.example.restapi.home.data.model

class CardModel {
    var title: String? = "Card Title"
    var body: String? = "Card Body"
    var imagePath: String? = null
    var id: Long? = 0
    var type: Int? = 0
    var postId: Long? = 0
    var userId: Long? = 0
    var place: Long? = 0

    constructor(movie: MovieModel) {
        imagePath = movie.posterPath
        title = movie.title
        body = movie.overview
        id = movie.id
        type = 1
    }

    constructor(actor: ActorModel) {
        imagePath = actor.profilePath
        title = actor.name
        body = "Actor known for " + actor.knownForDepartment
        id = actor.id
        type = 2
    }

    constructor(user: UserModel) {
        title = user.nickname
        body = "User " + user.nickname
        id = user.id
        type = 3
    }

    constructor(post: PostModel) {
        val title = post.title
        val author = post.authorNickname
        this.title = "$title by $author"
        this.body = post.content
        this.id = post.id
        this.type = 4
    }

    constructor(comment: CommentModel) {
        title = comment.authorNickname
        body = comment.content
        id = comment.id
        type = 5
        postId = comment.postId
        this.userId = comment.authorId
    }

    constructor(user: LeaderboardModel) {
        title = user.nickname
        body = user.totalLikes.toString() + " likes"
        id = user.id
        place = user.place
        type = 3
    }
}