package com.example.bec_client.activity

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
    private lateinit var actorImage : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actor)

        actorName = findViewById(R.id.actor_name)
        actorBiography = findViewById(R.id.actor_biography)
        actorImage = findViewById(R.id.actor_image)

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
                    val requestOptions = RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)

                    if(actor.profilePath != null)
                    {
                        Glide.with(this)
                            .applyDefaultRequestOptions(requestOptions)
                            .load(actor.profilePath)
                            .into(actorImage)
                    }
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