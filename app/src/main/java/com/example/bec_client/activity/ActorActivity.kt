package com.example.bec_client.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.bec_client.R

class ActorActivity : AppCompatActivity() {
    var id : Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actor)

        id = intent.getLongExtra("id",-1)
        if(id == -1L)
            throw Exception("Malformed Actor Activity")
        Log.d("Creating Actor Activity Instance", "id $id")
    }
}