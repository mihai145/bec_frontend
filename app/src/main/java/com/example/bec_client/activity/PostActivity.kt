package com.example.bec_client.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.bec_client.adapter.CommentAdapter
import com.example.restapi.home.data.model.CardModel
import com.example.restapi.home.data.model.PostModel
import com.example.restapi.home.data.model.request.PostInfoModel
import com.example.restapi.home.data.model.request.PostLikedModel
import com.example.restapi.home.data.model.response.SimpleResponseModel
import com.example.restapi.home.viewmodel.SearchViewModel
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostActivity : AppCompatActivity() {
    private var id: Long = 0

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var recyclerAdapter: CommentAdapter
    private lateinit var post: PostModel

    private lateinit var postTitle: TextView
    private lateinit var authorName: TextView
    private lateinit var movieName: TextView
    private lateinit var postContent: TextView
    private lateinit var likesCount: TextView

    private lateinit var editButton: Button
    private lateinit var addComment: Button
    private lateinit var deleteButton: Button
    private lateinit var likeButton: ToggleButton
    private var pressed: Boolean = false

    private var apiInterface: ApiInterface? = null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    override fun onResume() {
        id = intent.getLongExtra("id", -1)
        searchViewModel.wasLikedPost(id, MainActivity.userId?.toLong() ?: -1L)
        searchViewModel.getLikesPost(id)
        likesCount.text = ""
        feedData(id)
        super.onResume()
        if (pressed) {
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
                            movieName.text = "no movie"
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
        likesCount = findViewById(R.id.likesCount)

        editButton = findViewById(R.id.editPost)
        addComment = findViewById(R.id.addComment)
        deleteButton = findViewById(R.id.deletePost)
        likeButton = findViewById(R.id.likeButton)

        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        searchViewModel.postInfo(id)
        searchViewModel.wasLikedPost(id, MainActivity.userId?.toLong() ?: -1L)
        searchViewModel.getLikesPost(id)

        editButton.setOnClickListener {
            if ((MainActivity.userId?.toLong() ?: -1L) != post.authorId && !MainActivity.isAdmin) {
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
            if ((MainActivity.userId?.toLong() ?: -1L) != post.authorId && !MainActivity.isAdmin) {
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
                )?.enqueue(object : Callback<SimpleResponseModel> {
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

        likeButton.setOnClickListener {
            val requestModel = PostLikedModel(id, MainActivity.userId?.toLong() ?: -1L)
            if (likeButton.isChecked) {
                apiInterface?.likePost(
                    MainActivity.cachedCredentials?.idToken.toString(),
                    requestModel
                )
                    ?.enqueue(object : Callback<SimpleResponseModel> {
                        override fun onResponse(
                            call: Call<SimpleResponseModel>,
                            response: Response<SimpleResponseModel>
                        ) {
                            val res = response.body()
                            if (response.code() == 202 && res != null && res.ok) {
                                Toast.makeText(
                                    applicationContext,
                                    "Post was liked",
                                    Toast.LENGTH_SHORT
                                ).show()
                                likesCount.text = "You + " + likesCount.text
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<SimpleResponseModel>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                "Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } else {
                apiInterface?.deleteLikePost(
                    MainActivity.cachedCredentials?.idToken.toString(),
                    requestModel
                )
                    ?.enqueue(object : Callback<SimpleResponseModel> {
                        override fun onResponse(
                            call: Call<SimpleResponseModel>,
                            response: Response<SimpleResponseModel>
                        ) {
                            val res = response.body()
                            if (response.code() == 202 && res != null && res.ok) {
                                Toast.makeText(
                                    applicationContext,
                                    "Like was deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                                likesCount.text = likesCount.text.drop(6)
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<SimpleResponseModel>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                "Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
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
                        movieName.text = "no movie"
                    }
                } else {
                    Toast.makeText(this, "Post data not available", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Log.d("DEBUG: ", "a crapat")
            }
        })

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                rootView.context, LinearLayoutManager.VERTICAL, false
            )
            recyclerAdapter = CommentAdapter()
            adapter = recyclerAdapter
        }

    }

    private fun feedData(id: Long) {
        // populate recycler
        if (MainActivity.cachedCredentials != null) {
            recyclerAdapter.resetAdapter()
            searchViewModel.getComments(id)
        }
        searchViewModel.commentsLiveData?.observe(this, Observer {
            if (it != null) {
                recyclerAdapter.submitList(it.comments.map { x -> CardModel(x) })
                Log.d("DebugPosts", it.toString())
            } else {
                Log.d("DEBUG POSTS:", "a crapat")
            }
        })

        searchViewModel.wasLiked?.observe(this, Observer {
            Log.d("Was Liked", searchViewModel.wasLiked!!.value.toString())
            if (it != null) {
                if (it == 1) {
                    likeButton.isChecked = true
                    likesCount.text = "You + " + likesCount.text
                } else {
                    likeButton.isChecked = false
                }
                Log.d("Debug Liked", it.toString())
            }
        })

        searchViewModel.likes?.observe(this, Observer {
            Log.d("Likes", searchViewModel.likes!!.value.toString())
            if (it != null) {
                var real = it
                if(likeButton.isChecked)
                    real -= 1
                likesCount.text = "${likesCount.text}$real"
                Log.d("Debug Likes", it.toString())
            }
        })
    }
}