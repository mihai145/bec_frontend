package com.example.restapi.home.data.model

class CardModel {
    var title:String?="Card Title"
    var body:String?="Card Body"

    constructor(movie:MovieModel){
        title = movie.title
        body = movie.overview
    }
    constructor(actor:ActorModel){
        title = actor.name
        body = "Actor known for " + actor.knownForDepartment
    }

    constructor(user:UserModel) {
        title = user.nickname
        body = "User " + user.nickname
    }
}