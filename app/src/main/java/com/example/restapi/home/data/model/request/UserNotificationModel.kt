package com.example.restapi.home.data.model.request

class UserNotificationModel {
    var userId: Int? = 0

    constructor(userId: Long) {
        this.userId = userId.toInt()
    }
}