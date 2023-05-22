package com.example.restapi.home.data.model

data class LeaderboardModel (
    var id:Long,
    var nickname:String,
    var email:String,
    var place: Long,
    var totalLikes: Long,
)