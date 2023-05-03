package com.example.bec_client.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.restapi.home.data.model.request.EditPostModel
import com.example.restapi.home.data.model.request.SubmitPostModel
import com.example.restapi.home.data.model.response.SimpleResponseModel
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import retrofit2.Call
import retrofit2.Response

class PostFormActivity : AppCompatActivity() {
    private var postId: Long = 0
    private var movieId: Long? = 0
    private var movieName: String? = ""
    private var newOrEdit: Long = 0
    private var title: String = ""
    private var content: String = ""

    private lateinit var newPostTitle: TextView
    private lateinit var movieNameTextView: TextView
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var submitButton: Button
    private var pressed: Boolean = false

    private var apiInterface: ApiInterface? = null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_form)

        postId = intent.getLongExtra("postId", -1)
        movieId = intent.getLongExtra("movieId", -1)
        if (movieId == -1L) movieId = null
        movieName = intent.getStringExtra("movieName").toString()
        if (movieName == "") movieName = null

        newOrEdit = intent.getLongExtra("newOrEdit", -1)
        if (newOrEdit == -1L)
            throw Exception("Malformed Post Form Activity")

        title = intent.getStringExtra("title").toString()
        content = intent.getStringExtra("content").toString()

        newPostTitle = findViewById(R.id.newPostTitle)
        if (newOrEdit == 1L) newPostTitle.text = "EDIT POST"

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        submitButton = findViewById(R.id.submitButton)
        movieNameTextView = findViewById(R.id.movieNameTextView)

        titleEditText.setText(title)
        contentEditText.setText(content)
        if (movieName != null) movieNameTextView.text = movieName

        // get user id from id token...
        val userId = if (MainActivity.userId == null) (-1) else MainActivity.userId!!

        submitButton.setOnClickListener {
            if (!pressed) {
                if (newOrEdit == 0L) {
                    // new post logic
                    pressed = true

                    val requestModel = SubmitPostModel(
                        userId,
                        titleEditText.text.toString(),
                        contentEditText.text.toString(),
                        movieId,
                        movieName
                    )
                    apiInterface?.submitPost(
                        MainActivity.cachedCredentials?.idToken.toString(),
                        requestModel
                    )?.enqueue(object : retrofit2.Callback<SimpleResponseModel> {
                        override fun onResponse(
                            call: Call<SimpleResponseModel>,
                            response: Response<SimpleResponseModel>
                        ) {
                            val res = response.body()
                            if (response.code() == 202 && res != null && res.ok) {
                                Toast.makeText(applicationContext, "Added", Toast.LENGTH_SHORT)
                                    .show()
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
                            Toast.makeText(
                                applicationContext,
                                "Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                            pressed = false
                        }
                    })
                } else {
                    // edit logic
                    pressed = true

                    val requestModel = EditPostModel(
                        postId.toInt(),
                        titleEditText.text.toString(),
                        contentEditText.text.toString()
                    )
                    apiInterface?.editPost(
                        MainActivity.cachedCredentials?.idToken.toString(),
                        requestModel
                    )?.enqueue(object : retrofit2.Callback<SimpleResponseModel> {
                        override fun onResponse(
                            call: Call<SimpleResponseModel>,
                            response: Response<SimpleResponseModel>
                        ) {
                            val res = response.body()
                            if (response.code() == 202 && res != null && res.ok == true) {
                                Toast.makeText(applicationContext, "Edited", Toast.LENGTH_SHORT)
                                    .show()
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
                            Toast.makeText(
                                applicationContext,
                                "Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                            pressed = false
                        }
                    })
                }
            }
        }
    }
}