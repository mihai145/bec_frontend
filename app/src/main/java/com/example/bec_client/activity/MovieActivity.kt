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
import com.bumptech.glide.request.RequestOptions
import com.example.bec_client.R
import com.example.restapi.home.data.model.CardModel
import com.example.restapi.home.data.model.MovieModel
import com.example.restapi.home.viewmodel.SearchViewModel

class MovieActivity : AppCompatActivity() {
    private var id : Long = 0
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var movie : MovieModel

    private lateinit var moviePoster: ImageView
    private lateinit var movieTitle: TextView
    private lateinit var movieReleaseDate: TextView
    private lateinit var movieOverview: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        moviePoster = findViewById(R.id.movie_poster)
        movieTitle = findViewById(R.id.movie_title)
        movieReleaseDate = findViewById(R.id.movie_release_date)
        movieOverview = findViewById(R.id.movie_overview)

        id = intent.getLongExtra("id",-1)
        if(id == -1L)
            throw Exception("Malformed Movie Activity")
        Log.d("Creating Movie Activity Instance", "id $id")

        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        searchViewModel.movieInfo(id)

        searchViewModel.movieInfoLiveData?.observe(this, Observer {
            if (it!=null){
                movie = it.result
                Log.d("RENDERING",movie.toString())
                if (movie != null) {
                    val requestOptions = RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)

                    if(movie.posterPath != null)
                    {
                        Glide.with(this)
                            .applyDefaultRequestOptions(requestOptions)
                            .load(movie.posterPath)
                            .into(moviePoster)
                    }
                    movieTitle.text = movie.title
                    movieReleaseDate.text = movie.releaseDate
                    movieOverview.text = movie.overview
                } else {
                    Toast.makeText(this, "Movie data not available", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }else{
                Log.d("DEBUG: ", "a crapat")
            }
        })

    }
}