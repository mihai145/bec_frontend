package com.example.bec_client.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.restapi.home.data.model.MovieModel
import com.example.restapi.home.data.model.request.DidReviewModel
import com.example.restapi.home.data.model.response.DidReviewResponseModel
import com.example.restapi.home.viewmodel.SearchViewModel
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import retrofit2.Call
import retrofit2.Response

class MovieActivity : AppCompatActivity() {
    private var id: Long = 0
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var movie: MovieModel

    private lateinit var moviePoster: ImageView
    private lateinit var movieTitle: TextView
    private lateinit var movieReleaseDate: TextView
    private lateinit var movieOverview: TextView

    private lateinit var reviewButton: Button
    private var pressed: Boolean = false

    private var apiInterface: ApiInterface? = null
    private var postId: Long = 0

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    override fun onResume() {
        super.onResume()
        if(pressed) {
            pressed = false

            val userId = if (MainActivity.userId == null) (-1) else MainActivity.userId!!

            if (userId != -1) {
                val requestModel = DidReviewModel(userId.toLong(), id)
                apiInterface?.didIReview(
                    MainActivity.cachedCredentials?.idToken.toString(),
                    requestModel
                )
                    ?.enqueue(object : retrofit2.Callback<DidReviewResponseModel> {
                        override fun onResponse(
                            call: Call<DidReviewResponseModel>,
                            response: Response<DidReviewResponseModel>
                        ) {
                            val res = response.body()
                            if (response.code() == 202 && res != null && res.ok) {
                                postId = res.postId.toLong()
                                if (res.reviewed) reviewButton.text = "SEE REVIEW"
                                else reviewButton.text = "WRITE REVIEW"
                            } else {
                                Log.d("DEBUG:", "A crapat")
                            }
                        }

                        override fun onFailure(call: Call<DidReviewResponseModel>, t: Throwable) {
                            Log.d("DEBUG:", "A crapat")
                        }
                    })
            } else {
                reviewButton.text = "WRITE REVIEW"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        moviePoster = findViewById(R.id.moviePoster)
        movieTitle = findViewById(R.id.movieTitle)
        movieReleaseDate = findViewById(R.id.movieReleaseDate)
        movieOverview = findViewById(R.id.movieOverview)

        id = intent.getLongExtra("id", -1)
        if (id == -1L)
            throw Exception("Malformed Movie Activity")
        Log.d("Creating Movie Activity Instance", "id $id")

        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        searchViewModel.movieInfo(id)

        searchViewModel.movieInfoLiveData?.observe(this, Observer {
            if (it != null) {
                movie = it.result
                Log.d("RENDERING", movie.toString())
                if (movie != null) {
                    val requestOptions = RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)

                    if (movie.posterPath != null) {
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
            } else {
                Log.d("DEBUG: ", "a crapat")
            }
        })

        // get user id from id token...
        val userId = if (MainActivity.userId == null) (-1) else MainActivity.userId!!

        reviewButton = findViewById(R.id.reviewButton)
        reviewButton.setOnClickListener {
            pressed = true
            if (reviewButton.text != "LOADING") {
                if (MainActivity.cachedCredentials == null) {
                    Toast.makeText(this, "You need to be logged in to do that", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (reviewButton.text == "SEE REVIEW") {
                        val intent = Intent(this, PostActivity::class.java)
                        intent.putExtra("id", postId)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, PostFormActivity::class.java)
                        intent.putExtra("newOrEdit", 0L)
                        intent.putExtra("movieId", id)
                        intent.putExtra("movieName", movie.title)
                        intent.putExtra("title", "")
                        intent.putExtra("content", "")
                        startActivity(intent)
                    }
                }
            }
        }

        if (userId != -1) {
            val requestModel = DidReviewModel(userId.toLong(), id)
            apiInterface?.didIReview(
                MainActivity.cachedCredentials?.idToken.toString(),
                requestModel
            )
                ?.enqueue(object : retrofit2.Callback<DidReviewResponseModel> {
                    override fun onResponse(
                        call: Call<DidReviewResponseModel>,
                        response: Response<DidReviewResponseModel>
                    ) {
                        val res = response.body()
                        if (response.code() == 202 && res != null && res.ok) {
                            postId = res.postId.toLong()
                            if (res.reviewed) reviewButton.text = "SEE REVIEW"
                            else reviewButton.text = "WRITE REVIEW"
                        } else {
                            Log.d("DEBUG:", "A crapat")
                        }
                    }

                    override fun onFailure(call: Call<DidReviewResponseModel>, t: Throwable) {
                        Log.d("DEBUG:", "A crapat")
                    }
                })
        } else {
            reviewButton.text = "WRITE REVIEW"
        }
    }
}