package com.example.restapi.home.data.model.request

class UserFollowModel {
    var followerId:Int? = 0
    var followeeId:Int? = 0

    constructor(followerId: Long, followeeId: Long)
    {
        this.followerId = followerId.toInt()
        this.followeeId = followeeId.toInt()
    }
}