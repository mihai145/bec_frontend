package com.example.bec_client.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.bec_client.R
import com.example.restapi.home.data.model.UserModel
import com.example.restapi.home.viewmodel.SearchViewModel

class UserActivity : AppCompatActivity() {
    private var id : Long = 0
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var user : UserModel

    private lateinit var username: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        username = findViewById(R.id.username)
        username.text = "Ai dat click pe un user"

    }
}