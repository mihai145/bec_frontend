package com.example.bec_client.activity

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
import com.example.restapi.home.data.model.request.UserDeleteModel
import com.example.restapi.home.data.model.request.UserFollowModel
import com.example.restapi.home.data.model.response.SimpleResponseModel
import com.example.restapi.home.viewmodel.SearchViewModel
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserActivity : AppCompatActivity() {
    private var id: Long = 0
    private var nickname: String = ""
    private var following: Boolean = false
    private lateinit var searchViewModel: SearchViewModel
//    private lateinit var user : UserModel

    private lateinit var username: TextView
    private lateinit var followButton: Button
    private lateinit var deleteButton: Button

    private var apiInterface: ApiInterface? = null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        username = findViewById(R.id.username)

        id = intent.getLongExtra("id", -1)
        Log.d("Creating User Activity Instance", "id $id")

        nickname = intent.getStringExtra("nickname").toString()
        if (id == -1L || nickname == "")
            throw Exception("Malformed User Activity")
        username.text = "User $nickname"

        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        // get user id from id token...
        val userId = if (MainActivity.userId == null) (-1) else MainActivity.userId!!
        searchViewModel.userFollowInfo(userId.toLong(), id)

        followButton = findViewById(R.id.followButton)
        followButton.setOnClickListener {
            if (followButton.text != "LOADING") {
                if (MainActivity.cachedCredentials == null) {
                    Toast.makeText(this, "You need to be logged in to do that", Toast.LENGTH_SHORT)
                        .show()
                } else if (userId.toLong() == id) {
                    Toast.makeText(this, "You cannot follow yourself", Toast.LENGTH_SHORT).show()
                } else {
                    if (following) {
                        // unfollow logic
                        followButton.text = "LOADING"

                        val requestModel = UserFollowModel(userId.toLong(), id)
                        apiInterface?.unfollow(
                            MainActivity.cachedCredentials?.idToken.toString(),
                            requestModel
                        )?.enqueue(object :
                            Callback<SimpleResponseModel> {
                            override fun onFailure(call: Call<SimpleResponseModel>, t: Throwable) {
                                Toast.makeText(
                                    applicationContext,
                                    "Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                                followButton.text = "UNFOLLOW"
                            }

                            override fun onResponse(
                                call: Call<SimpleResponseModel>,
                                response: Response<SimpleResponseModel>
                            ) {
                                val res = response.body()
                                if (response.code() == 202 && res != null && res.ok == true) {
                                    following = !following
                                    followButton.text = "FOLLOW"
                                    Toast.makeText(
                                        applicationContext,
                                        "Unfollowed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Please try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    followButton.text = "UNFOLLOW"
                                }
                            }
                        })
                    } else {
                        // follow logic
                        followButton.text = "LOADING"

                        val requestModel = UserFollowModel(userId.toLong(), id)
                        apiInterface?.follow(
                            MainActivity.cachedCredentials?.idToken.toString(),
                            requestModel
                        )?.enqueue(object :
                            Callback<SimpleResponseModel> {
                            override fun onFailure(call: Call<SimpleResponseModel>, t: Throwable) {
                                Toast.makeText(
                                    applicationContext,
                                    "Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                                followButton.text = "FOLLOW"
                            }

                            override fun onResponse(
                                call: Call<SimpleResponseModel>,
                                response: Response<SimpleResponseModel>
                            ) {
                                val res = response.body()
                                if (response.code() == 202 && res != null && res.ok) {
                                    following = !following
                                    followButton.text = "UNFOLLOW"
                                    Toast.makeText(
                                        applicationContext,
                                        "Followed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Please try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    followButton.text = "FOLLOW"
                                }
                            }
                        })
                    }
                }
            }
        }

        deleteButton = findViewById(R.id.deleteUserButton)
        deleteButton.setOnClickListener {
            if (MainActivity.cachedCredentials == null) {
                Toast.makeText(this, "You need to be logged in to do that", Toast.LENGTH_SHORT)
                    .show()
            } else if (userId.toLong() == id) {
                Toast.makeText(this, "You cannot delete yourself", Toast.LENGTH_SHORT).show()
            } else if (!MainActivity.isAdmin) {
                Toast.makeText(this, "You are not an admin", Toast.LENGTH_SHORT).show()
            } else {
                // delete user logic
                followButton.text = "LOADING"

                val requestModel = UserDeleteModel(id.toInt())

                apiInterface?.deleteUser(
                    MainActivity.cachedCredentials?.idToken.toString(),
                    requestModel
                )?.enqueue(object :
                    Callback<SimpleResponseModel> {
                    override fun onFailure(call: Call<SimpleResponseModel>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            "Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (following) {
                            followButton.text = "UNFOLLOW"
                        } else {
                            followButton.text = "FOLLOW"
                        }
                    }

                    override fun onResponse(
                        call: Call<SimpleResponseModel>,
                        response: Response<SimpleResponseModel>
                    ) {
                        val res = response.body()
                        if (response.code() == 202 && res != null && res.ok) {
                            Toast.makeText(
                                applicationContext,
                                "Deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (following) {
                                followButton.text = "UNFOLLOW"
                            } else {
                                followButton.text = "FOLLOW"
                            }
                        }
                    }
                })
            }
        }

        searchViewModel.userFollowLiveData?.observe(this, Observer {
            if (it != null) {
                following = it.following
                if (following) {
                    followButton.text = "UNFOLLOW"
                } else {
                    followButton.text = "FOLLOW"
                }
                Log.d("FOLLOWING: ", following.toString())
            } else {
                if (MainActivity.cachedCredentials == null) {
                    followButton.text = "FOLLOW"
                }
                Log.d("DEBUG:", "A crapat")
            }
        })
    }
}