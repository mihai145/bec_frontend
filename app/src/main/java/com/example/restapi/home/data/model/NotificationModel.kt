package com.example.restapi.home.data.model

import com.google.gson.annotations.SerializedName
data class NotificationModel (
    val notificationId: Long? = 0,
    val message: String? = "",
    val postId: Long? = 0,
)