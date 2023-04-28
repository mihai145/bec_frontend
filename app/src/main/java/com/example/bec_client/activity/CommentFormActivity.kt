package com.example.bec_client.activity

import android.content.Intent
import android.util.Log
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.restapi.home.data.model.request.*
import com.example.restapi.home.data.model.response.SimpleResponseModel
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import retrofit2.Call
import retrofit2.Response

class CommentFormActivity : AppCompatActivity() {
    private var postId: Long = 0
    private var commentId: Long = 0
    private var authorId: Long = 0
    private var newOrEdit: Long = 0
    private var content: String = ""

    private lateinit var newCommentTitle: TextView
    private lateinit var contentEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var deleteButton: Button
    private var pressed: Boolean = false

    private var apiInterface: ApiInterface? = null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_form)

        postId = intent.getLongExtra("postId", -1)
        authorId = intent.getLongExtra("userId", -1)
        commentId = intent.getLongExtra("id", -1)

        newOrEdit = intent.getLongExtra("newOrEdit", -1)
        if (newOrEdit == -1L)
            throw Exception("Malformed Comment Form Activity")

        content = intent.getStringExtra("content").toString()

        newCommentTitle = findViewById(R.id.newCommentTitle)
        if (newOrEdit == 1L) newCommentTitle.text = "EDIT COMMENT"

        contentEditText = findViewById(R.id.contentEditText)
        submitButton = findViewById(R.id.submitButton)
        deleteButton = findViewById(R.id.deleteButton)
        contentEditText.setText(content)

        // get user id from id token...
        val userId = if (MainActivity.userId == null) (-1) else MainActivity.userId!!

        deleteButton.setOnClickListener {
            if(newOrEdit == 0L) {
                Toast.makeText(
                    applicationContext,
                    "You can't delete a comment that was not posted yet",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if ((MainActivity.userId?.toLong() ?: -1L) != authorId.toLong() && !MainActivity.isAdmin) {
                    Toast.makeText(
                        applicationContext,
                        "You can delete only your comments",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if(!pressed){
                    pressed = true
                    val requestModel = CommentInfoModel(commentId)
                    apiInterface?.deleteComment(
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

        }

        submitButton.setOnClickListener {
            if ((MainActivity.userId?.toLong() ?: -1L) != authorId && newOrEdit == 1L && !MainActivity.isAdmin) {
                Toast.makeText(
                    applicationContext,
                    "You can only edit your comments",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!pressed) {
                if (newOrEdit == 0L) {
                    // new comment logic
                    pressed = true

                    val requestModel = SubmitCommentModel(
                        userId,
                        contentEditText.text.toString(),
                        postId,
                    )
                    apiInterface?.submitComment(
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

                    val requestModel = EditCommentModel(
                        commentId.toInt(),
                        contentEditText.text.toString()
                    )
                    apiInterface?.editComment(
                        MainActivity.cachedCredentials?.idToken.toString(),
                        requestModel
                    )?.enqueue(object : retrofit2.Callback<SimpleResponseModel> {
                        override fun onResponse(
                            call: Call<SimpleResponseModel>,
                            response: Response<SimpleResponseModel>
                        ) {
                            val res = response.body()
                            if (response.code() == 202 && res != null && res.ok) {
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