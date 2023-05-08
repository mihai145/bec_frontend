package com.example.restapi.home.data.model.response

import com.example.restapi.home.data.model.LeaderboardModel

data class LeaderboardResponseModel (
    var ok: Boolean,
    var users: List<LeaderboardModel>
)