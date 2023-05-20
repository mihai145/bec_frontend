package com.example.restapi.home.data.model.request

class NotificationReqModel {
    var userId: Int? = 0

    constructor(userId: Long) {
        this.userId = userId.toInt()
    }
}