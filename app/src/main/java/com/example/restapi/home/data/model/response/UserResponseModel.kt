package com.example.restapi.home.data.model.response

import com.example.restapi.home.data.model.UserModel

data class UserSearchModel (
    val ok: Boolean,
    val results: List<UserModel>
)