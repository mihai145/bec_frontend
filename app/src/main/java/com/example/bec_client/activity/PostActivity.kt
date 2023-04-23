package com.example.bec_client.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.bec_client.adapter.RecyclerAdapter
import com.example.restapi.home.data.model.CardModel
import com.example.restapi.home.data.model.PostModel
import com.example.restapi.home.data.model.request.PostInfoModel
import com.example.restapi.home.data.model.response.SimpleResponseModel
import com.example.restapi.home.viewmodel.SearchViewModel
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import com.google.android.gms.common.api.internal.LifecycleActivity
import retrofit2.Call
import retrofit2.Response

class PostActivity : AppCompatActivity() {
    private var id: Long = 0

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var post: PostModel

    private lateinit var postTitle: TextView
    private lateinit var authorName: TextView
    private lateinit var movieName: TextView
    private lateinit var postContent: TextView

    private lateinit var editButton: Button
    private lateinit var addComment: Button
    private lateinit var deleteButton: Button
    private var pressed: Boolean = false

    private var apiInterface: ApiInterface? = null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    override fun onResume() {
        super.onResume()
        if(pressed) {
            pressed = false
            searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
            searchViewModel.postInfo(id)
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
        addComment = findViewById(R.id.addComment)
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

        addComment.setOnClickListener {
            if (!pressed) {
                pressed = true
                val intent = Intent(this, CommentFormActivity::class.java)
                intent.putExtra("newOrEdit", 0L)
                intent.putExtra("content", "Content")
                intent.putExtra("postId", post.id)
                intent.putExtra("userId", MainActivity.userId)
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
                            finish()
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

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                rootView.context
                ,LinearLayoutManager.VERTICAL, false
            )
            recyclerAdapter = RecyclerAdapter()
            adapter = recyclerAdapter
        }

        feedData(id)
        searchViewModel.commentsLiveData?.observe(this, Observer {
            if (it != null) {
                recyclerAdapter.submitList(it.comments.map { x -> CardModel(x) })
                Log.d("DebugPosts", it.toString())
            } else {
                Log.d("DEBUG POSTS:", "a crapat")
            }
        })
    }

    override fun onResume() {
        id = intent.getLongExtra("id", -1)
        feedData(id)
        super.onResume()
    }

    fun feedData(id: Long) {
        // populate recycler
        if (MainActivity.cachedCredentials != null) {
            recyclerAdapter.resetAdapter()
            searchViewModel.getComments(id)
        }
    }
}