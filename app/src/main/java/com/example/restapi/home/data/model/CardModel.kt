package com.example.restapi.home.data.model

class CardModel {
    var title:String?="Card Title"
    var body:String?="Card Body"
    var imagePath:String?=null
    var id:Long?=0
    var type:Int?=0

    constructor(movie:MovieModel){
        imagePath = movie.posterPath
        title = movie.title
        body = movie.overview
        id = movie.id
        type = 1
    }
    constructor(actor:ActorModel){
        imagePath = actor.profilePath
        title = actor.name
        body = "Actor known for " + actor.knownForDepartment
        id = actor.id
        type = 2
    }
}