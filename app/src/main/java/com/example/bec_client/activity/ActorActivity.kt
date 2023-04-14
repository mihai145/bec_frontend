package com.example.bec_client.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bec_client.R
import com.example.restapi.home.data.model.ActorModel
import com.example.restapi.home.data.model.MovieModel
import com.example.restapi.home.viewmodel.SearchViewModel

class ActorActivity : AppCompatActivity() {
    private var id : Long = 0
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var actor : ActorModel

    private lateinit var actorName: TextView
    private lateinit var actorBiography: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actor)

        actorName = findViewById(R.id.actor_name)
        actorBiography = findViewById(R.id.actor_biography)

        id = intent.getLongExtra("id",-1)
        if(id == -1L)
            throw Exception("Malformed Actor Activity")
        Log.d("Creating Actor Activity Instance", "id $id")

        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        searchViewModel.actorInfo(id)

        searchViewModel.actorInfoLiveData?.observe(this, Observer {
            if (it!=null){
                actor = it.result
                Log.d("RENDERING",actor.toString())
                if (actor != null) {
                    actorName.text = actor.name
                    actorBiography.text = actor.biography
                } else {
                    Toast.makeText(this, "Actor data not available", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }else{
                Log.d("DEBUG: ", "a crapat")
            }
        })

    }
}