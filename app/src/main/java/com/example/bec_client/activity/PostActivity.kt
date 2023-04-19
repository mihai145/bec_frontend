package com.example.bec_client.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.restapi.home.data.model.PostModel
import com.example.restapi.home.data.model.request.PostInfoModel
import com.example.restapi.home.data.model.response.SimpleResponseModel
import com.example.restapi.home.viewmodel.SearchViewModel
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import retrofit2.Call
import retrofit2.Response

class PostActivity : AppCompatActivity() {
    private var id: Long = 0

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var post: PostModel

    private lateinit var postTitle: TextView
    private lateinit var authorName: TextView
    private lateinit var movieName: TextView
    private lateinit var postContent: TextView

    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private var pressed: Boolean = false

    private var apiInterface: ApiInterface? = null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        id = intent.getLongExtra("id", -1)
        if (id == -1L)
            throw Exception("Malformed Post Activity")
        Log.d("Creating Post Activity Instance", "id $id")

        postTitle = findViewById(R.id.postTitle)
        authorName = findViewById(R.id.authorName)
        movieName = findViewById(R.id.movieName)
        postContent = findViewById(R.id.postContent)

        editButton = findViewById(R.id.editPost)
        deleteButton = findViewById(R.id.deletePost)

        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        searchViewModel.postInfo(id)

        editButton.setOnClickListener {
            if ((MainActivity.userId?.toLong() ?: -1L) != post.authorId) {
                Toast.makeText(
                    applicationContext,
                    "You can only edit your own posts",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!pressed) {
                pressed = true
                val intent = Intent(this, PostFormActivity::class.java)
                intent.putExtra("newOrEdit", 1L)
                intent.putExtra("postId", post.id)
                intent.putExtra("movieId", post.movieId)
                intent.putExtra("movieName", post.movieName)
                intent.putExtra("title", post.title)
                intent.putExtra("content", post.content)
                startActivity(intent)
            }
        }

        deleteButton.setOnClickListener {
            if ((MainActivity.userId?.toLong() ?: -1L) != post.authorId) {
                Toast.makeText(
                    applicationContext,
                    "You can only delete your own posts",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!pressed) {
                pressed = true
                val requestModel = PostInfoModel(id)
                apiInterface?.deletePost(
                    MainActivity.cachedCredentials?.idToken.toString(),
                    requestModel
                )?.enqueue(object : retrofit2.Callback<SimpleResponseModel> {
                    override fun onResponse(
                        call: Call<SimpleResponseModel>,
                        response: Response<SimpleResponseModel>
                    ) {
                        val res = response.body()
                        if (response.code() == 202 && res != null && res.ok == true) {
                            Toast.makeText(applicationContext, "Deleted", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, MovieActivity::class.java)
                            intent.putExtra("id", post.movieId)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                            pressed = false
                        }
                    }

                    override fun onFailure(call: Call<SimpleResponseModel>, t: Throwable) {
                        Toast.makeText(applicationContext, "Please try again", Toast.LENGTH_SHORT)
                            .show()
                        pressed = false
                    }
                })
            }
        }

        searchViewModel.postInfoLiveData?.observe(this, Observer {
            if (it != null) {
                post = it.post
                if (post != null) {
                    val title = post.title
                    val author = post.authorNickname
                    postTitle.text = "Title: $title"
                    authorName.text = "Author: $author"
                    postContent.text = post.content
                    if (post.movieName != null) {
                        val mvName = post.movieName
                        movieName.text = "Movie: $mvName"
                    } else {
                        movieName.text = "Normal Post, does not reffer to any movie"
                    }
                } else {
                    Toast.makeText(this, "Post data not available", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Log.d("DEBUG: ", "a crapat")
            }
        })
    }
}