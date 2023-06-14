package com.example.restapi.home.data.model.response

import com.example.restapi.home.data.model.NotificationModel

data class NotificationResponseModel (
    val ok: Boolean,
    val results: List<NotificationModel>
)