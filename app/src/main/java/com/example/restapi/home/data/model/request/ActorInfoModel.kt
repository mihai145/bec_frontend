package com.example.restapi.home.data.model.request

class ActorInfoModel {
    var actorId:Int? = 0
    constructor(actorId : Long)
    {
        this.actorId = actorId.toInt()
    }
}